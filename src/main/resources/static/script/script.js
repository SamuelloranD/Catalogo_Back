function atualizarPreco(idPreco, preco) {
    document.getElementById(idPreco).innerText = `R$ ${parseFloat(preco).toFixed(2).replace('.', ',')}`;
}

const produtoContainer = document.getElementById('produtos');
const produtos = Array.from(produtoContainer.children);

produtos.sort((a, b) => {
    const nomeA = a.getAttribute('data-nome').toLowerCase();
    const nomeB = b.getAttribute('data-nome').toLowerCase();
    return nomeA.localeCompare(nomeB);
});

produtos.forEach(produto => produtoContainer.appendChild(produto));

const produtosContainer = document.getElementById('produtos');
const produto = Array.from(produtosContainer.getElementsByClassName('produto'));
const btnVerMais = document.getElementById('ver-mais');
let produtosMostrados = 15;

// Função para mostrar os produtos iniciais
function mostrarProdutos() {
    produtos.slice(0, produtosMostrados).forEach(produto => {
        produto.style.display = 'block';
    });
}

// Inicialmente, esconda todos os produtos e mostre apenas os dois primeiros
produtos.forEach(produto => produto.style.display = 'none');
mostrarProdutos();

// Clique no botão "Ver Mais" para exibir mais produtos
btnVerMais.addEventListener('click', () => {
    produtosMostrados += 15;
    mostrarProdutos();

    // Se todos os produtos estiverem visíveis, esconda o botão
    if (produtosMostrados >= produtos.length) {
        btnVerMais.style.display = 'none';
    }
});
