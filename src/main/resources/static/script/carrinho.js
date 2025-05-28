document.addEventListener('DOMContentLoaded', () => {
    const carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];
    const container = document.getElementById('carrinho-itens');
    const totalElement = document.getElementById('total');

    renderizarCarrinho();
    atualizarContadorCarrinho();

    function atualizarContadorCarrinho() {
        const carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];
        const totalItens = carrinho.reduce((total, item) => total + item.quantidade, 0);
        const contador = document.querySelector('.carrinho-count');

        if (contador) {
            contador.textContent = totalItens;
            contador.style.display = totalItens > 0 ? 'block' : 'none';
        }
    }

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

        atualizarTotal();
        atualizarContadorCarrinho();
    }

    function atualizarTotal() {
        const total = carrinho.reduce((sum, item) => sum + (item.preco * item.quantidade), 0);
        totalElement.textContent = total.toFixed(2);
    }

    function salvarCarrinho() {
        localStorage.setItem('carrinho', JSON.stringify(carrinho));
    }

    container.addEventListener('click', (e) => {
        console.log('Clique detectado:', e.target);

        if (e.target.classList.contains('btn-quantidade') || e.target.closest('.btn-quantidade')) {
            const button = e.target.classList.contains('btn-quantidade') ? e.target : e.target.closest('.btn-quantidade');
            const id = button.dataset.id;
            const volume = button.dataset.volume;
            // Corrigido para comparar strings e evitar undefined
            const item = carrinho.find(i => String(i.id) === id && String(i.volume) === volume);

            if (!item) {
                console.warn('Item não encontrado no carrinho:', id, volume);
                return;
            }

            if (button.textContent === '+') {
                item.quantidade++;
            } else if (button.textContent === '-' && item.quantidade > 1) {
                item.quantidade--;
            }

            salvarCarrinho();
            renderizarCarrinho();
        }

        if (e.target.classList.contains('btn-remover') || e.target.closest('.btn-remover')) {
            const button = e.target.classList.contains('btn-remover') ? e.target : e.target.closest('.btn-remover');
            const id = button.dataset.id;
            const volume = button.dataset.volume;
            const index = carrinho.findIndex(i => String(i.id) === id && String(i.volume) === volume);

            if (index !== -1) {
                carrinho.splice(index, 1);
                salvarCarrinho();
                renderizarCarrinho();
            }
        }
    });

    document.getElementById('finalizar-compra').addEventListener('click', finalizarCompra);

    async function finalizarCompra() {
        if (carrinho.length === 0) {
            alert('Seu carrinho está vazio!');
            return;
        }

        const itens = carrinho.map(item => ({
            produtoId: item.id,
            quantidade: item.quantidade,
            precoUnitario: item.preco
        }));

        const valorTotal = itens.reduce((soma, item) => soma + item.precoUnitario * item.quantidade, 0);

        const venda = {
            itens: itens,
            valorTotal: valorTotal
        };

        try {
            const token = localStorage.getItem('token');
            console.log("Token JWT:", token);

            if (!token) {
                alert("Você precisa estar logado para finalizar a compra.");
                return;
            }

            const response = await fetch("http://localhost:8080/vendas", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(venda)
            });

            if (response.ok) {
                const mensagem = formatarMensagemWhatsApp();
                const mensagemCodificada = encodeURIComponent(mensagem);
                const numeroWhatsApp = '558399016170';

                window.open(`https://wa.me/${numeroWhatsApp}?text=${mensagemCodificada}`, '_blank');
                localStorage.removeItem('carrinho');
                atualizarContadorCarrinho();
                renderizarCarrinho();
            } else {
                alert('Erro ao registrar a venda. Faça login novamente ou tente mais tarde.');
            }
        } catch (error) {
            console.error('Erro ao enviar venda:', error);
            alert('Erro de conexão. Verifique sua internet.');
        }
    }

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

window.adicionarAoCarrinho = function(produto) {
    let carrinho = JSON.parse(localStorage.getItem('carrinho')) || [];

    const itemExistente = carrinho.find(item =>
        String(item.id) === String(produto.id) && String(item.volume) === String(produto.volume)
    );

    if (itemExistente) {
        itemExistente.quantidade++;
    } else {
        carrinho.push(produto);
    }

    localStorage.setItem('carrinho', JSON.stringify(carrinho));
    // Atualiza contador no header
    const contador = document.querySelector('.carrinho-count');
    if (contador) {
        const totalItens = carrinho.reduce((total, item) => total + item.quantidade, 0);
        contador.textContent = totalItens;
        contador.style.display = totalItens > 0 ? 'block' : 'none';
    }
};
