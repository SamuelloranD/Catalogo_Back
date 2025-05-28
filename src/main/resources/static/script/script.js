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

document.addEventListener('DOMContentLoaded', async () => {
    const path = window.location.pathname;

    if (path.includes('masculino100.html')) {
        await carregarProdutosPorCategoria('masculino');
    } else if (path.includes('feminino100.html')) {
        await carregarProdutosPorCategoria('feminino');
    } else {
        await carregarTodosProdutos();
    }

    configurarLogin();
    atualizarContadorCarrinho();
});

async function carregarProdutosPorCategoria(categoria) {
    const container = document.getElementById('produtos');
    const categoriaFormatada = categoria.charAt(0).toUpperCase() + categoria.slice(1);

    try {
        const response = await fetch(`/api/novos-produtos?categoria=${categoria}`);
        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const produtos = await response.json();
        console.log(`Produtos ${categoriaFormatada}:`, produtos);

        renderizarProdutos(produtos);

        const tituloSecao = document.querySelector('#masculinos h2, #femininos h2');
        if (tituloSecao) {
            tituloSecao.textContent = `Perfumes ${categoriaFormatada}s`;
        }

    } catch (error) {
        console.error(`Erro ao carregar produtos ${categoria}:`, error);
        mostrarErroCarregamento(container);
    }
}


async function carregarTodosProdutos() {
    const container = document.getElementById('produtos');
    const btnVerMais = document.getElementById('ver-mais');

    try {
        const response = await fetch('/api/novos-produtos');
        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const produtos = await response.json();
        console.log("Todos os produtos:", produtos);

        renderizarProdutos(produtos.slice(0, 12), true);

        btnVerMais.addEventListener('click', () => {
            const produtosMostrados = document.querySelectorAll('.produto').length;
            renderizarProdutos(produtos.slice(produtosMostrados, produtosMostrados + 12));

            if (produtosMostrados + 12 >= produtos.length) {
                btnVerMais.style.display = 'none';
            }
        });


    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
        mostrarErroCarregamento(container);
    }
}

function renderizarProdutos(produtos, limpar = false) {
    const container = document.getElementById('produtos');
    if (limpar) container.innerHTML = '';

    const htmlProdutos = produtos.map(produto => {
        if (produto.categoria.toLowerCase() === 'hidratante') {
            return `
                <div class="produto" data-id="${produto.id}" data-categoria="${produto.categoria}">
                    <img src="${produto.imagemUrl || 'imagens/sem-imagem.jpg'}" alt="${produto.nome}">
                    <h3>${produto.nome}</h3>
                    <p class="descricao">${produto.descricao || ''}</p>
                    <p class="preco">R$ ${produto.preco.toFixed(2).replace('.', ',')}</p>
                    <button class="add-to-cart" onclick="adicionarAoCarrinho(${produto.id}, null, ${produto.preco})">
                        Adicionar ao Carrinho
                    </button>
                </div>
            `;
        } else {
            return `
                <div class="produto" data-id="${produto.id}" data-categoria="${produto.categoria}">
                    <img src="${produto.imagemUrl || 'imagens/sem-imagem.jpg'}" alt="${produto.nome}">
                    <h3>${produto.nome}</h3>
                    <p class="descricao">${produto.descricao || ''}</p>

                    <div class="volume-selector">
                        <label>Volume:</label>
                        <select onchange="atualizarPreco('preco-${produto.id}', this.value)">
                            <option value="${produto.preco55ml}">55ml</option>
                            <option value="${produto.preco100ml}" selected>100ml</option>
                        </select>
                    </div>

                    <p class="preco" id="preco-${produto.id}">R$ ${produto.preco100ml.toFixed(2).replace('.', ',')}</p>

                    <button class="add-to-cart" onclick="adicionarAoCarrinho(${produto.id}, this.previousElementSibling.querySelector('select').value, null)">
                        Adicionar ao Carrinho
                    </button>
                </div>
            `;
        }
    }).join('');

    container.insertAdjacentHTML('beforeend', htmlProdutos);
}



function atualizarPreco(idPreco, valor) {
    const precoElement = document.getElementById(idPreco);
    if (precoElement) {
        const preco = parseFloat(valor).toFixed(2).replace('.', ',');
        precoElement.textContent = `R$ ${preco}`;
    }
}

function adicionarAoCarrinho(produtoId, precoSelecionado, precoFixo) {
    const usuarioLogado = localStorage.getItem('isLoggedIn') === 'true';

    if (!usuarioLogado) {
        alert("Você precisa estar logado para adicionar itens ao carrinho.");
        window.location.href = "login.html";
        return;
    }

    const produtoElement = document.querySelector(`.produto[data-id="${produtoId}"]`);
    const volumeSelecionado = precoSelecionado ?
        produtoElement.querySelector('select').options[produtoElement.querySelector('select').selectedIndex].text :
        (produtoElement.dataset.categoria.toLowerCase() === 'hidratante' ? produtoElement.querySelector('.preco').textContent : null);

    const preco = precoSelecionado ? parseFloat(precoSelecionado) : precoFixo;

    const produto = {
        id: produtoId,
        nome: produtoElement.querySelector('h3').textContent,
        preco: preco,
        quantidade: 1,
        volume: volumeSelecionado ? volumeSelecionado.replace('ml', '').trim() : null,
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
                localStorage.removeItem('carrinho');
                atualizarContadorCarrinho()
                window.location.href = 'index.html';
            } else {
                window.location.href = 'login.html';
            }
        };
    }
}