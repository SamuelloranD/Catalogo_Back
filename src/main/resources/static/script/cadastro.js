document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('form-cadastro');
    const mensagem = document.getElementById('mensagem');

    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        const formData = new FormData(form);

        try {
            const response = await fetch('/enviar', {
                method: 'POST',
                body: formData
            });

            const responseText = await response.text();

            if (response.ok) {
                mensagem.style.display = 'block';
                mensagem.style.color = 'green'; // cor de sucesso
                mensagem.textContent = responseText;

                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 5000);
            } else {
                mensagem.style.display = 'block';
                mensagem.style.color = 'red'; // cor de erro
                mensagem.textContent = responseText;
            }
        } catch (error) {
            console.error('Erro ao cadastrar usuário:', error);
            alert('Ocorreu um erro ao tentar cadastrar.');
        }
    });
});
