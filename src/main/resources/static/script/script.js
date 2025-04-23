// -------------------- FUNÇÕES GERAIS --------------------
function atualizarPreco(idPreco, preco) {
    document.getElementById(idPreco).innerText = `R$ ${parseFloat(preco).toFixed(2).replace('.', ',')}`;
}

// -------------------- CARREGAMENTO DINÂMICO --------------------
let produtosMostrados = 9; // Quantidade inicial (ajuste conforme seu layout)
let todosProdutos = []; // Armazena todos os produtos carregados
const btnVerMais = document.getElementById('ver-mais');

async function carregarProdutos() {
    try {
        // Carrega TODOS os produtos da API (ou ajuste para uma categoria específica)
        const response = await fetch('/api/produtos');
        todosProdutos = await response.json();

        // Renderiza os primeiros produtos
        renderizarProdutos(todosProdutos.slice(0, produtosMostrados));

        // Mostra/oculta o botão "Ver mais"
        btnVerMais.style.display = todosProdutos.length > produtosMostrados ? 'block' : 'none';

    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
    }
}

function renderizarProdutos(produtos) {
    const container = document.getElementById('produtos');
    container.innerHTML = produtos.map(produto => `
        <div class="produto" data-nome="${produto.nome}">
            <img src="${produto.imagemUrl}" alt="${produto.nome}">
            <h3>${produto.nome}</h3>
            <div class="volume-selector">
                <label>Volume:</label>
                <select onchange="atualizarPreco('preco-${produto.codigo}', this.value)">
                    <option value="${produto.preco55ml}">55 ml</option>
                    <option value="${produto.preco100ml}" selected>100 ml</option>
                </select>
            </div>
            <p class="preco" id="preco-${produto.codigo}">R$ ${produto.preco100ml.toFixed(2).replace('.', ',')}</p>
            <button class="add-to-cart" 
                    data-id="${produto.codigo}" 
                    data-nome="${produto.nome}" 
                    data-preco="${produto.preco100ml}">
                Adicionar ao Carrinho
            </button>
        </div>
    `).join('');
}

// Evento do botão "Ver mais"
btnVerMais?.addEventListener('click', () => {
    produtosMostrados += 9; // Aumenta a quantidade exibida
    renderizarProdutos(todosProdutos.slice(0, produtosMostrados));

    // Oculta o botão se não houver mais produtos
    if (produtosMostrados >= todosProdutos.length) {
        btnVerMais.style.display = 'none';
    }
});

// -------------------- CONTROLE DE LOGIN/ADMIN --------------------
document.addEventListener('DOMContentLoaded', () => {
    carregarProdutos(); // Inicia o carregamento

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
                window.location.href = '/index.html';
            } else {
                window.location.href = '/login.html';
            }
        };
    }
});

// -------------------- CARRINHO DE COMPRAS --------------------
let carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];

function atualizarCarrinho() {
    localStorage.setItem('carrinho', JSON.stringify(carrinho));

    // Atualiza o contador do carrinho
    const contador = document.querySelector('.carrinho-count');
    if (contador) {
        const totalItens = carrinho.reduce((total, item) => total + item.quantidade, 0);
        contador.textContent = totalItens;
        contador.style.display = totalItens > 0 ? 'block' : 'none';
    }

    // Atualiza o dropdown do carrinho (se existir)
    atualizarDropdownCarrinho();
}

function atualizarDropdownCarrinho() {
    const dropdown = document.querySelector('.carrinho-dropdown');
    if (dropdown) {
        dropdown.innerHTML = carrinho.length === 0
            ? '<p class="carrinho-vazio">Seu carrinho está vazio</p>'
            : carrinho.map(item => `
                <div class="carrinho-item">
                    <img src="${item.imagem}" alt="${item.nome}">
                    <div>
                        <h4>${item.nome}</h4>
                        <p>${item.volume} - R$ ${item.preco.toFixed(2)} x ${item.quantidade}</p>
                    </div>
                </div>
            `).join('');
    }
}

// Adicionar ao carrinho
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('add-to-cart')) {
        const produtoElemento = e.target.closest('.produto');
        const volumeSelecionado = produtoElemento.querySelector('select').value;
        const preco = parseFloat(e.target.dataset.preco);

        const produto = {
            id: e.target.dataset.id,
            nome: e.target.dataset.nome,
            preco: preco,
            quantidade: 1,
            volume: volumeSelecionado === e.target.dataset.preco ? '100ml' : '55ml',
            imagem: produtoElemento.querySelector('img').src
        };

        const itemExistente = carrinho.find(item => item.id === produto.id && item.volume === produto.volume);
        if (itemExistente) {
            itemExistente.quantidade++;
        } else {
            carrinho.push(produto);
        }

        atualizarCarrinho();

        // Feedback visual
        e.target.textContent = '✔ Adicionado';
        setTimeout(() => {
            e.target.textContent = 'Adicionar ao Carrinho';
        }, 2000);
    }
});