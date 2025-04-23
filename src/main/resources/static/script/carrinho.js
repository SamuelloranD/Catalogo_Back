document.addEventListener('DOMContentLoaded', () => {
    const carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];
    const container = document.getElementById('carrinho-itens');
    const totalElement = document.getElementById('total');

    // Renderiza o carrinho ao carregar a página
    renderizarCarrinho();

    // Função para renderizar os itens do carrinho
    function renderizarCarrinho() {
        if (carrinho.length === 0) {
            container.innerHTML = '<p class="carrinho-vazio">Seu carrinho está vazio</p>';
            totalElement.textContent = '0.00';
            return;
        }

        container.innerHTML = carrinho.map(item => `
            <div class="carrinho-item">
                <img src="${item.imagem}" alt="${item.nome}">
                <div class="item-info">
                    <h3>${item.nome}</h3>
                    <p>${item.volume} - R$ ${item.preco.toFixed(2)}</p>
                    <div class="item-controles">
                        <button class="btn-quantidade" data-id="${item.id}" data-volume="${item.volume}">-</button>
                        <span>${item.quantidade}</span>
                        <button class="btn-quantidade" data-id="${item.id}" data-volume="${item.volume}">+</button>
                    </div>
                </div>
                <button class="btn-remover" data-id="${item.id}" data-volume="${item.volume}">
                    <img src="imagens/lixeira-icon.png" alt="Remover">
                </button>
            </div>
        `).join('');

        // Atualiza o total
        atualizarTotal();
    }

    // Atualiza o valor total
    function atualizarTotal() {
        const total = carrinho.reduce((sum, item) => sum + (item.preco * item.quantidade), 0);
        totalElement.textContent = total.toFixed(2);
    }

    // Atualiza o carrinho no localStorage
    function salvarCarrinho() {
        localStorage.setItem('carrinho', JSON.stringify(carrinho));
    }

    // Eventos para os botões de quantidade
    container.addEventListener('click', (e) => {
        // Aumentar/Diminuir quantidade
        if (e.target.classList.contains('btn-quantidade') || e.target.closest('.btn-quantidade')) {
            const button = e.target.classList.contains('btn-quantidade') ? e.target : e.target.closest('.btn-quantidade');
            const id = button.dataset.id;
            const volume = button.dataset.volume;
            const item = carrinho.find(i => i.id === id && i.volume === volume);

            if (button.textContent === '+') {
                item.quantidade++;
            } else if (item.quantidade > 1) {
                item.quantidade--;
            }

            salvarCarrinho();
            renderizarCarrinho();
        }

        // Remover item
        if (e.target.classList.contains('btn-remover') || e.target.closest('.btn-remover')) {
            const button = e.target.classList.contains('btn-remover') ? e.target : e.target.closest('.btn-remover');
            const id = button.dataset.id;
            const volume = button.dataset.volume;
            const index = carrinho.findIndex(i => i.id === id && i.volume === volume);

            carrinho.splice(index, 1);
            salvarCarrinho();
            renderizarCarrinho();
        }
    });

    // Finalizar compra via WhatsApp
    document.getElementById('finalizar-compra').addEventListener('click', finalizarCompra);

    // Função para finalizar a compra
    function finalizarCompra() {
        if (carrinho.length === 0) {
            alert('Seu carrinho está vazio!');
            return;
        }

        const mensagem = formatarMensagemWhatsApp();
        const mensagemCodificada = encodeURIComponent(mensagem);
        const numeroWhatsApp = '558399016170'; // Substitua pelo seu número

        window.open(`https://wa.me/${numeroWhatsApp}?text=${mensagemCodificada}`, '_blank');

        // Opcional: Limpar o carrinho após finalizar
        // carrinho.length = 0;
        // salvarCarrinho();
        // renderizarCarrinho();
    }

    // Formata a mensagem para o WhatsApp
    function formatarMensagemWhatsApp() {
        let mensagem = '🛍️ *PEDIDO - RH KOSMETIC* 🛍️\n\n';
        mensagem += 'Olá, gostaria de comprar os seguintes produtos:\n\n';

        let total = 0;

        carrinho.forEach((produto, index) => {
            const subtotal = produto.preco * produto.quantidade;
            mensagem += `*${produto.nome}* (${produto.volume})\n`;
            mensagem += `Quantidade: ${produto.quantidade}\n`;
            mensagem += `Preço unitário: R$ ${produto.preco.toFixed(2)}\n`;
            mensagem += `Subtotal: R$ ${subtotal.toFixed(2)}\n\n`;

            total += subtotal;
        });

        mensagem += `*TOTAL: R$ ${total.toFixed(2)}*\n\n`;
        mensagem += 'Por favor, confirme a disponibilidade e informe as formas de pagamento.\n';

        return mensagem;
    }
});