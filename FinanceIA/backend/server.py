"""
FinanceIA — Backend Flask com Google Gemini (grátis)
Roda em: python server.py
Acesso do emulador Android: http://10.0.2.2:5000

Como obter a chave GRATUITA:
1. Acesse: https://aistudio.google.com/app/apikey
2. Clique em "Create API Key"
3. Copie a chave gerada (começa com "AIza...")
4. No PowerShell: $env:GEMINI_API_KEY = "AIza..."
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import google.generativeai as genai
import os
import json

app = Flask(__name__)
CORS(app)

GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY", "")
genai.configure(api_key=GEMINI_API_KEY)

MODEL = "gemini-1.5-flash"

SYSTEM_PROMPT = """Você é a FinanceIA, uma assistente financeira pessoal inteligente e empática.

Suas características:
- Responde SEMPRE em português brasileiro
- Linguagem clara, direta e amigável
- Dá conselhos práticos e acionáveis
- Nunca inventa dados — se não tem informação, diz claramente
- Usa emojis moderadamente para deixar o texto mais agradável
- Respostas concisas (máximo 3-4 parágrafos salvo pedido contrário)

Quando receber contexto financeiro do usuário, use essas informações para personalizar os conselhos.
"""


def montar_prompt_sistema(contexto=None):
    prompt = SYSTEM_PROMPT
    if contexto:
        prompt += f"""

[CONTEXTO FINANCEIRO DO USUÁRIO - use para personalizar a resposta]
Saldo atual: R$ {contexto.get('saldo_atual', 0):.2f}
Receitas do mês: R$ {contexto.get('receitas_mes', 0):.2f}
Despesas do mês: R$ {contexto.get('despesas_mes', 0):.2f}
Top categorias de gasto: {', '.join(contexto.get('top_categorias', []))}
"""
    return prompt


@app.route("/api/chat", methods=["POST"])
def chat():
    data      = request.json
    mensagens = data.get("messages", [])
    contexto  = data.get("contexto_financeiro")

    if not mensagens:
        return jsonify({"erro": "Mensagem vazia"}), 400

    model = genai.GenerativeModel(
        model_name=MODEL,
        system_instruction=montar_prompt_sistema(contexto)
    )

    historico = []
    for msg in mensagens[:-1]:
        role = "user" if msg["role"] == "user" else "model"
        historico.append({"role": role, "parts": [msg["content"]]})

    ultima = mensagens[-1]["content"]
    chat_session = model.start_chat(history=historico)
    resposta = chat_session.send_message(ultima)

    return jsonify({"resposta": resposta.text, "tokens_usados": 0})


@app.route("/api/insight-mensal", methods=["POST"])
def insight_mensal():
    data     = request.json
    saldo    = data.get("saldo", 0)
    receitas = data.get("receitas", 0)
    despesas = data.get("despesas", 0)
    cats     = data.get("top_categorias", [])
    poupanca = ((receitas - despesas) / receitas * 100) if receitas > 0 else 0

    prompt = f"""Analise a situação financeira abaixo e dê um insight personalizado e prático (máximo 3 parágrafos):

Saldo do mês: R$ {saldo:.2f}
Receitas: R$ {receitas:.2f}
Despesas: R$ {despesas:.2f}
Taxa de poupança: {poupanca:.1f}%
Principais gastos: {', '.join(cats) if cats else 'Nenhum registro ainda'}

Seja direto, empático e dê pelo menos 1 sugestão prática."""

    model    = genai.GenerativeModel(MODEL, system_instruction=SYSTEM_PROMPT)
    resposta = model.generate_content(prompt)
    return jsonify({"resposta": resposta.text, "tokens_usados": 0})


@app.route("/api/analisar-extrato", methods=["POST"])
def analisar_extrato():
    data    = request.json
    extrato = data.get("extrato_texto", "")

    prompt = f"""Analise o extrato bancário abaixo e extraia as transações.

Extrato:
{extrato}

Retorne SOMENTE um JSON válido com esta estrutura (sem markdown, sem texto extra):
{{
  "transacoes": [
    {{
      "titulo": "nome da transação",
      "valor": 0.00,
      "tipo": "RECEITA" ou "DESPESA",
      "categoria": "uma de: Alimentação, Transporte, Moradia, Saúde, Lazer, Educação, Salário, Freelance, Investimentos, Outros",
      "data": "AAAA-MM-DD"
    }}
  ],
  "resumo": "breve resumo do extrato em 1-2 frases"
}}"""

    model    = genai.GenerativeModel(MODEL)
    resposta = model.generate_content(prompt)

    try:
        texto = resposta.text.strip().replace("```json", "").replace("```", "")
        return jsonify(json.loads(texto))
    except Exception:
        return jsonify({"transacoes": [], "resumo": "Não foi possível analisar o extrato."}), 200


@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "app":    "FinanceIA Backend",
        "model":  MODEL,
        "key_ok": bool(GEMINI_API_KEY)
    })


if __name__ == "__main__":
    if not GEMINI_API_KEY:
        print("=" * 50)
        print("AVISO: GEMINI_API_KEY nao configurada!")
        print("PowerShell: $env:GEMINI_API_KEY = 'AIza...'")
        print("=" * 50)
    app.run(host="0.0.0.0", port=5000, debug=True)
