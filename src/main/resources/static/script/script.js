function atualizarPreco(idPreco, preco) {
    document.getElementById(idPreco).innerText = `R$ ${parseFloat(preco).toFixed(2).replace('.', ',')}`;
}

// Ordenação de produtos por nome
const produtoContainer = document.getElementById('produtos');
const produtos = Array.from(produtoContainer.children);

produtos.sort((a, b) => {
    const nomeA = a.getAttribute('data-nome').toLowerCase();
    const nomeB = b.getAttribute('data-nome').toLowerCase();
    return nomeA.localeCompare(nomeB);
});

produtos.forEach(produto => produtoContainer.appendChild(produto));

// Exibição de produtos com "Ver Mais"
const produtosContainer = document.getElementById('produtos');
const produto = Array.from(produtosContainer.getElementsByClassName('produto'));
const btnVerMais = document.getElementById('ver-mais');
let produtosMostrados = 15;

function mostrarProdutos() {
    produto.slice(0, produtosMostrados).forEach(produto => {
        produto.style.display = 'block';
    });
}

// Inicialmente, esconda todos os produtos e mostre apenas os iniciais
produto.forEach(produto => produto.style.display = 'none');
mostrarProdutos();

btnVerMais.addEventListener('click', () => {
    produtosMostrados += 15;
    mostrarProdutos();

    if (produtosMostrados >= produto.length) {
        btnVerMais.style.display = 'none';
    }
});

// -------------------- ADMIN CONTROLE --------------------

// Função para pegar parâmetros da URL
function getParametroUrl(nome) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(nome);
}

// Detecta admin na URL e salva no localStorage
document.addEventListener('DOMContentLoaded', function() {
    const adminParam = getParametroUrl('admin');
    if (adminParam) {
        localStorage.setItem('admin', adminParam);
    }

    const isAdmin = localStorage.getItem('admin') === 'true';

    if (isAdmin) {
        const nav = document.querySelector('nav ul');
        const adminItem = document.createElement('li');
        const adminLink = document.createElement('a');
        adminLink.href = 'admin.html';
        adminLink.textContent = 'Painel Admin';
        adminItem.appendChild(adminLink);
        nav.appendChild(adminItem);
    }
});

// -------------------- LOGOUT --------------------

// ... (código existente de produtos)

// -------------------- LOGIN/LOGOUT --------------------
document.addEventListener('DOMContentLoaded', function() {
    const loginLink = document.getElementById('login-logout-link');
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

    if (loginLink) {
        if (isLoggedIn) {
            loginLink.textContent = 'Sair';
            loginLink.onclick = async (e) => {
                e.preventDefault();
                try {
                    await fetch('/logout');
                    localStorage.removeItem('isLoggedIn');
                    localStorage.removeItem('admin');
                    window.location.href = '/index.html';
                } catch (error) {
                    console.error("Erro no logout:", error);
                }
            };
        } else {
            loginLink.textContent = 'Login';
            loginLink.onclick = (e) => {
                e.preventDefault();
                window.location.href = '/login.html';
            };
        }
    }
});