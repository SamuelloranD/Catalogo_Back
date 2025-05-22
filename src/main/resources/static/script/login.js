document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const usuario = document.getElementById('usuario').value;
    const senha = document.getElementById('senha').value;
    const erroElement = document.getElementById('erro-login');

    erroElement.style.display = 'none';
    erroElement.textContent = '';

    try {
        const response = await fetch("http://localhost:8080/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({ usuario, senha })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('token', data.token);

            if (data.admin) {
                localStorage.setItem('admin', 'true');
            } else {
                localStorage.removeItem('admin');
            }

            window.location.href = '/index.html';
        }

        else {
            erroElement.textContent = data.message || 'Usuário ou senha incorretos!';
            erroElement.style.display = 'block';
            erroElement.style.animation = 'fadeIn 0.5s';

            document.getElementById('usuario').classList.add('campo-erro');
            document.getElementById('senha').classList.add('campo-erro');
        }
    } catch (error) {
        console.error('Erro no login:', error);
        erroElement.textContent = 'Erro ao conectar com o servidor. Tente novamente.';
        erroElement.style.display = 'block';
    }
});
