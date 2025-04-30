document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('produtos-container');

    try {
        // Busca produtos femininos do backend
        const response = await fetch('/api/produtos/categoria/feminino');
        const produtos = await response.json();

        // Gera os cards dos produtos
        container.innerHTML = produtos.map(produto => `
            <div class="produto-card">
                <img src="${produto.imagemUrl || 'imagens/sem-imagem.jpg'}" alt="${produto.nome}">
                <h3>${produto.nome}</h3>
                <p>${produto.descricao || ''}</p>
                
                <div class="volume-selector">
                    <label>
                        <input type="radio" name="volume-${produto.id}" value="55ml" checked>
                        55ml - R$ ${produto.preco55ml.toFixed(2)}
                    </label>
                    <label>
                        <input type="radio" name="volume-${produto.id}" value="100ml">
                        100ml - R$ ${produto.preco100ml.toFixed(2)}
                    </label>
                </div>
                
                <button class="btn-comprar" onclick="adicionarAoCarrinho(${produto.id})">
                    Adicionar ao Carrinho
                </button>
            </div>
        `).join('');

    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
        container.innerHTML = '<p>Erro ao carregar produtos. Tente novamente.</p>';
    }
});

function adicionarAoCarrinho(produtoId) {
    const volumeSelecionado = document.querySelector(`input[name="volume-${produtoId}"]:checked`).value;
    // Implemente a lógica do carrinho aqui (usando seu carrinho.js)
    console.log(`Produto ${produtoId} (${volumeSelecionado}) adicionado ao carrinho`);
}