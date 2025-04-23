document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const usuario = document.getElementById('usuario').value;
    const senha = document.getElementById('senha').value;
    const erroElement = document.getElementById('erro-login');

    // Reset do erro
    erroElement.style.display = 'none';
    erroElement.textContent = '';

    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `usuario=${encodeURIComponent(usuario)}&senha=${encodeURIComponent(senha)}`
        });

        if (response.ok) {
            const url = await response.text();
            localStorage.setItem('isLoggedIn', 'true');

            if (url.includes('admin=true')) {
                localStorage.setItem('admin', 'true');
            }

            window.location.href = url;
        } else {
            // Mostra mensagem de erro com animação
            erroElement.textContent = 'Usuário ou senha incorretos!';
            erroElement.style.display = 'block';
            erroElement.style.animation = 'fadeIn 0.5s';

            // Adiciona classe de erro nos campos
            document.getElementById('usuario').classList.add('campo-erro');
            document.getElementById('senha').classList.add('campo-erro');
        }
    } catch (error) {
        console.error('Erro no login:', error);
        erroElement.textContent = 'Erro ao conectar com o servidor. Tente novamente.';
        erroElement.style.display = 'block';
    }
});