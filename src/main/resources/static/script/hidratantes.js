document.addEventListener('DOMContentLoaded', async () => {
    await carregarHidratantes();
    configurarLogin();
    atualizarContadorCarrinho();
});

async function carregarHidratantes() {
    const container = document.getElementById('produtos');

    try {
        const response = await fetch('/api/hidratantes');
        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const hidratantes = await response.json();
        console.log("Hidratantes carregados:", hidratantes);

        renderizarHidratantes(hidratantes.slice(0, 12));

        // Configura o botão "Ver Mais"
        const btnVerMais = document.getElementById('ver-mais');
        if (btnVerMais && hidratantes.length > 12) {
            btnVerMais.style.display = 'block';
            btnVerMais.addEventListener('click', () => {
                const produtosMostrados = document.querySelectorAll('.produto').length;
                renderizarHidratantes(hidratantes.slice(0, produtosMostrados + 12));

                if (produtosMostrados + 12 >= hidratantes.length) {
                    btnVerMais.style.display = 'none';
                }
            });
        }
    } catch (error) {
        console.error("Erro ao carregar hidratantes:", error);
        mostrarErroCarregamento(container);
    }
}

function renderizarHidratantes(hidratantes) {
    const container = document.getElementById('produtos');
    container.innerHTML = hidratantes.map(hidratante => `
        <div class="produto" data-id="${hidratante.id}" data-categoria="hidratante">
            <img src="${hidratante.imagemUrl || 'imagens_hidratantes/sem-imagem.jpg'}" alt="${hidratante.nome}">
            <h3>${hidratante.nome}</h3>
            
            <!-- Volume fixo (diferente dos perfuses) -->
            <div class="volume-info">
                <span>${hidratante.volume || '60g'}</span>
            </div>
            
            <!-- Preço único -->
            <p class="preco">R$ ${hidratante.preco.toFixed(2).replace('.', ',')}</p>
            
            <!-- Botão de Comprar -->
            <button class="add-to-cart" onclick="adicionarHidratanteAoCarrinho(${hidratante.id})">
                Adicionar ao Carrinho
            </button>
        </div>
    `).join('');
}

function adicionarHidratanteAoCarrinho(hidratanteId) {
    const produtoElement = document.querySelector(`.produto[data-id="${hidratanteId}"]`);

    const produto = {
        id: hidratanteId,
        nome: produtoElement.querySelector('h3').textContent,
        preco: parseFloat(produtoElement.querySelector('.preco').textContent.replace('R$ ', '').replace(',', '.')),
        quantidade: 1,
        volume: produtoElement.querySelector('.volume-info span').textContent,
        imagem: produtoElement.querySelector('img').src,
        categoria: 'hidratante'
    };

    adicionarAoCarrinho(produto); // <-- usa a função genérica do carrinho.js

    // Feedback visual
    const btn = produtoElement.querySelector('.add-to-cart');
    btn.textContent = '✔ Adicionado';
    setTimeout(() => {
        btn.textContent = 'Adicionar ao Carrinho';
    }, 2000);
}


// Reutiliza as mesmas funções auxiliares dos perfumes
function atualizarContadorCarrinho() {
    const carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];
    const totalItens = carrinho.reduce((total, item) => total + item.quantidade, 0);
    const contador = document.querySelector('.carrinho-count');

    if (contador) {
        contador.textContent = totalItens;
        contador.style.display = totalItens > 0 ? 'block' : 'none';
    }
}

function mostrarErroCarregamento(container) {
    container.innerHTML = `
        <div class="error-message">
            Erro ao carregar produtos. Recarregue a página.
            <button onclick="window.location.reload()">Recarregar</button>
        </div>
    `;
}

function configurarLogin() {
    // Configuração idêntica à dos perfumes
    const adminParam = new URLSearchParams(window.location.search).get('admin');
    if (adminParam) localStorage.setItem('admin', adminParam);

    const isAdmin = localStorage.getItem('admin') === 'true';
    if (isAdmin) {
        const nav = document.querySelector('nav ul');
        if (nav) {
            const adminItem = document.createElement('li');
            adminItem.innerHTML = '<a href="admin.html">Painel Admin</a>';
            nav.appendChild(adminItem);
        }
    }

    const loginLink = document.getElementById('login-logout-link');
    if (loginLink) {
        const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
        loginLink.textContent = isLoggedIn ? 'Sair' : 'Login';
        loginLink.onclick = (e) => {
            e.preventDefault();
            if (isLoggedIn) {
                localStorage.removeItem('isLoggedIn');
                localStorage.removeItem('admin');
                window.location.href = 'index.html';
            } else {
                window.location.href = 'login.html';
            }
        };
    }
}