document.addEventListener('DOMContentLoaded', async () => {
    // Identifica qual página está sendo acessada
    const path = window.location.pathname;

    if (path.includes('masculino100.html')) {
        await carregarProdutosPorCategoria('masculino');
    } else if (path.includes('feminino100.html')) {
        await carregarProdutosPorCategoria('feminino');
    } else {
        await carregarTodosProdutos();
    }

    // Configurações de login e carrinho
    configurarLogin();
    atualizarContadorCarrinho();
});

// Função para carregar produtos por categoria
async function carregarProdutosPorCategoria(categoria) {
    const container = document.getElementById('produtos');
    const categoriaFormatada = categoria.charAt(0).toUpperCase() + categoria.slice(1);

    try {
        const response = await fetch(`/api/produtos/${categoria}s`);
        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const produtos = await response.json();
        console.log(`Produtos ${categoriaFormatada}:`, produtos);

        renderizarProdutos(produtos);

        // Atualiza o título da seção se necessário
        const tituloSecao = document.querySelector('#masculinos h2, #femininos h2');
        if (tituloSecao) {
            tituloSecao.textContent = categoriaFormatada;
        }

    } catch (error) {
        console.error(`Erro ao carregar produtos ${categoria}:`, error);
        mostrarErroCarregamento(container);
    }
}

// Função para carregar todos os produtos (página principal)
async function carregarTodosProdutos() {
    const container = document.getElementById('produtos');

    try {
        const response = await fetch('/api/produtos');
        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const produtos = await response.json();
        console.log("Todos os produtos:", produtos);

        renderizarProdutos(produtos.slice(0, 12));

        // Configura o botão "Ver Mais" se houver mais produtos
        const btnVerMais = document.getElementById('ver-mais');
        if (btnVerMais && produtos.length > 12) {
            btnVerMais.style.display = 'block';
            btnVerMais.addEventListener('click', () => {
                const produtosMostrados = document.querySelectorAll('.produto').length;
                renderizarProdutos(produtos.slice(0, produtosMostrados + 12));

                if (produtosMostrados + 12 >= produtos.length) {
                    btnVerMais.style.display = 'none';
                }
            });
        }

    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
        mostrarErroCarregamento(container);
    }
}

// Função para renderizar os produtos na tela
function renderizarProdutos(produtos) {
    const container = document.getElementById('produtos');
    container.innerHTML = produtos.map(produto => `
        <div class="produto" data-id="${produto.id}" data-categoria="${produto.categoria}">
            <img src="${produto.imagemUrl || 'imagens/sem-imagem.jpg'}" alt="${produto.nome}">
            <h3>${produto.nome}</h3>
            <p class="marca">${produto.marca || ''}</p>
            
            <!-- Seletor de Volume -->
            <div class="volume-selector">
                <label>Volume:</label>
                <select onchange="atualizarPreco('preco-${produto.id}', this.value, this)">
                    <option style="text-align: center" value="${produto.preco55ml}">55ml</option>
                    <option style="text-align: center" value="${produto.preco100ml}" selected>100ml</option>
                </select>
            </div>
            
            <!-- Preço (será atualizado pelo seletor) -->
            <p class="preco" id="preco-${produto.id}">R$ ${produto.preco100ml.toFixed(2).replace('.', ',')}</p>
            
            <!-- Botão de Comprar -->
            <button class="add-to-cart" onclick="adicionarAoCarrinho(${produto.id})">
                Adicionar ao Carrinho
            </button>
        </div>
    `).join('');
}

// Funções auxiliares
function atualizarPreco(idPreco, valor, selectElement) {
    const precoElement = document.getElementById(idPreco);
    if (precoElement) {
        const preco = parseFloat(valor).toFixed(2).replace('.', ',');
        precoElement.textContent = `R$ ${preco}`;
    }
}

function adicionarAoCarrinho(produtoId) {
    // Verifica se o usuário está logado (exemplo: token no localStorage)
    const usuarioLogado = localStorage.getItem('isLoggedIn') === 'true';

    if (!usuarioLogado) {
        alert("Você precisa estar logado para adicionar itens ao carrinho.");
        window.location.href = "login.html"; // Redireciona para login
        return; // Interrompe a função
    }

    // Se estiver logado, continua o processo original
    const produtoElement = document.querySelector(`.produto[data-id="${produtoId}"]`);
    const selectVolume = produtoElement.querySelector('select');
    const volumeSelecionado = selectVolume.options[selectVolume.selectedIndex].text;
    const preco = parseFloat(selectVolume.value);

    const produto = {
        id: produtoId,
        nome: produtoElement.querySelector('h3').textContent,
        preco: preco,
        quantidade: 1,
        volume: volumeSelecionado.split(' - ')[0],
        imagem: produtoElement.querySelector('img').src,
        categoria: produtoElement.dataset.categoria
    };

    let carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];
    const itemExistente = carrinho.find(item =>
        item.id === produto.id && item.volume === produto.volume
    );

    if (itemExistente) {
        itemExistente.quantidade++;
    } else {
        carrinho.push(produto);
    }

    localStorage.setItem('carrinho', JSON.stringify(carrinho));
    atualizarContadorCarrinho();

    // Feedback visual
    const btn = produtoElement.querySelector('.add-to-cart');
    btn.textContent = '✔ Adicionado';
    setTimeout(() => {
        btn.textContent = 'Adicionar ao Carrinho';
    }, 2000);
}

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
    if (container) {
        container.innerHTML = `
            <div class="error-message">
                Erro ao carregar produtos. Recarregue a página.
                <button onclick="window.location.reload()">Recarregar</button>
            </div>
        `;
    }
}

function configurarLogin() {
    // Configura login/admin (mantido do seu código original)
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

    // Controle de login/logout
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