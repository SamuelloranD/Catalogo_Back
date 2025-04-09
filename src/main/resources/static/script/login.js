const urlParams = new URLSearchParams(window.location.search);
const erro = urlParams.get('erro');

if (erro) {
    document.getElementById('erro').textContent = 'Usuário ou senha incorretos. Cadastre-se!';
}