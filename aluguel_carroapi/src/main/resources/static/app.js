const lojaForm = document.querySelector("#loja-form");
const carroForm = document.querySelector("#carro-form");
const clienteForm = document.querySelector("#cliente-form");
const aluguelForm = document.querySelector("#aluguel-form");
const lojaSelect = document.querySelector("#idLoja");
const aluguelCarroSelect = document.querySelector("#aluguel-carro");
const carroSubmit = document.querySelector("#carro-submit");
const refreshButton = document.querySelector("#refresh-button");
const tabela = document.querySelector("#carros-tabela");
const currency = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
let lojas = [];
let carros = [];
let clientes = [];
let alugueis = [];
let lojaEditId = null;
let carroEditId = null;
let clienteEditId = null;

async function requestJson(url, options = {}) {
    let response;
    try {
        response = await fetch(url, options);
    } catch {
        throw new Error("Não foi possível conectar à API. Verifique se o Spring Boot está rodando.");
    }

    if (!response.ok) {
        const fallback = `A API respondeu com erro ${response.status}.`;
        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            const details = await response.json().catch(() => null);
            throw new Error(details?.mensagem || details?.detail || details?.message || fallback);
        }
        throw new Error(fallback);
    }

    return response.status === 204 || response.status === 201
        ? null
        : response.json();
}

function showFeedback(id, message, kind) {
    const element = document.getElementById(id);
    element.textContent = message;
    element.className = `feedback ${kind}`;
}

function addCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "—";
    row.appendChild(cell);
    return cell;
}

function showTableMessage(tbody, columns, message) {
    tbody.replaceChildren();
    const row = document.createElement("tr");
    const cell = addCell(row, message);
    cell.colSpan = columns;
    cell.className = "table-message";
    tbody.appendChild(row);
}

function addActions(row, actions) {
    const cell = document.createElement("td");
    const group = document.createElement("div");
    group.className = "action-cell";
    for (const action of actions) {
        const button = document.createElement("button");
        button.type = "button";
        button.className = `small-button ${action.danger ? "danger" : ""}`;
        button.textContent = action.label;
        button.disabled = Boolean(action.disabled);
        button.addEventListener("click", action.run);
        group.appendChild(button);
    }
    cell.appendChild(group);
    row.appendChild(cell);
}

function formatDate(value) {
    return value ? value.split("-").reverse().join("/") : "—";
}

function formatCpf(value) {
    return value?.length === 11
        ? `${value.slice(0, 3)}.${value.slice(3, 6)}.${value.slice(6, 9)}-${value.slice(9)}`
        : value || "—";
}

function renderLojas() {
    const tbody = document.querySelector("#lojas-tabela");
    tbody.replaceChildren();
    document.querySelector("#total-lojas").textContent = lojas.length;
    if (!lojas.length) {
        showTableMessage(tbody, 3, "Nenhuma loja cadastrada.");
        return;
    }
    for (const loja of lojas) {
        const row = document.createElement("tr");
        addCell(row, loja.cidade);
        addCell(row, loja.endereco);
        addActions(row, [
            { label: "Editar", run: () => editarLoja(loja) },
            { label: "Excluir", danger: true, run: () => excluirRecurso(`/loja/${loja.id}`, "loja", "loja-feedback") }
        ]);
        tbody.appendChild(row);
    }
}

function renderCarros(carros) {
    tabela.replaceChildren();
    document.querySelector("#total-carros").textContent = carros.length;
    document.querySelector("#carros-disponiveis").textContent =
        carros.filter(carro => carro.statusCarro === "DISPONIVEL").length;

    if (carros.length === 0) {
        showTableMessage(tabela, 6, "Nenhum carro cadastrado.");
        return;
    }

    for (const carro of carros) {
        const row = document.createElement("tr");
        addCell(row, carro.modelo);
        addCell(row, carro.categoria);
        addCell(row, carro.cidadeLoja);
        addCell(row, carro.precoFixo == null ? "—" : currency.format(carro.precoFixo));
        const statusCell = document.createElement("td");
        const badge = document.createElement("span");
        const disponivel = carro.statusCarro === "DISPONIVEL";
        badge.className = `badge ${disponivel ? "available" : "rented"}`;
        badge.textContent = disponivel ? "Disponível" : "Alugado";
        statusCell.appendChild(badge);
        row.appendChild(statusCell);
        addActions(row, [
            { label: "Editar", disabled: !disponivel, run: () => editarCarro(carro) },
            { label: "Excluir", danger: true, disabled: !disponivel,
                run: () => excluirRecurso(`/carros/${carro.id}`, "carro", "carro-feedback") }
        ]);
        tabela.appendChild(row);
    }
}

function renderClientes() {
    const tbody = document.querySelector("#clientes-tabela");
    tbody.replaceChildren();
    if (!clientes.length) {
        showTableMessage(tbody, 4, "Nenhum cliente cadastrado.");
        return;
    }
    for (const cliente of clientes) {
        const row = document.createElement("tr");
        addCell(row, cliente.nome);
        addCell(row, formatCpf(cliente.cpf));
        addCell(row, cliente.telefone);
        addActions(row, [
            { label: "Editar", run: () => editarCliente(cliente) },
            { label: "Excluir", danger: true,
                run: () => excluirRecurso(`/cliente/${cliente.id}`, "cliente", "cliente-feedback") }
        ]);
        tbody.appendChild(row);
    }
}

function renderAlugueis() {
    const tbody = document.querySelector("#alugueis-tabela");
    tbody.replaceChildren();
    if (!alugueis.length) {
        showTableMessage(tbody, 8, "Nenhum aluguel registrado.");
        return;
    }
    for (const aluguel of alugueis) {
        const row = document.createElement("tr");
        addCell(row, aluguel.modeloCarro);
        addCell(row, aluguel.nomeCliente);
        addCell(row, formatCpf(aluguel.cpfCliente));
        addCell(row, formatDate(aluguel.data));
        addCell(row, aluguel.tempo);
        addCell(row, currency.format(aluguel.preco));
        const statusCell = document.createElement("td");
        const badge = document.createElement("span");
        badge.className = `badge ${aluguel.dataDevolucao ? "finished" : "rented"}`;
        badge.textContent = aluguel.dataDevolucao ? "Devolvido" : "Ativo";
        statusCell.appendChild(badge);
        row.appendChild(statusCell);
        addActions(row, aluguel.dataDevolucao ? [] : [
            { label: "Devolver", run: () => devolverAluguel(aluguel) }
        ]);
        tbody.appendChild(row);
    }
}

async function loadLojas() {
    const selectedId = lojaSelect.value;
    lojas = await requestJson("/loja");
    renderLojas();
    lojaSelect.replaceChildren();

    const placeholder = document.createElement("option");
    placeholder.value = "";
    placeholder.textContent = lojas.length ? "Selecione uma loja" : "Cadastre uma loja primeiro";
    lojaSelect.appendChild(placeholder);

    for (const loja of lojas) {
        const option = document.createElement("option");
        option.value = loja.id;
        option.textContent = `${loja.cidade} — ${loja.endereco}`;
        lojaSelect.appendChild(option);
    }
    lojaSelect.value = lojas.some(loja => loja.id === selectedId) ? selectedId : "";
    carroSubmit.disabled = lojas.length === 0;
}

async function loadCarros() {
    const selectedId = aluguelCarroSelect.value;
    carros = await requestJson("/carros");
    renderCarros(carros);
    const disponiveis = carros.filter(carro => carro.statusCarro === "DISPONIVEL");
    aluguelCarroSelect.replaceChildren(new Option(
        disponiveis.length ? "Selecione um carro" : "Nenhum carro disponível", ""));
    for (const carro of disponiveis) {
        aluguelCarroSelect.appendChild(new Option(
            `${carro.modelo} — ${carro.cidadeLoja} — ${currency.format(carro.precoFixo)}/dia`, carro.id));
    }
    aluguelCarroSelect.value = disponiveis.some(carro => carro.id === selectedId) ? selectedId : "";
    atualizarTotal();
}

async function loadClientes() {
    clientes = await requestJson("/cliente");
    renderClientes();
}

async function loadAlugueis() {
    alugueis = await requestJson("/alugueis");
    renderAlugueis();
}

async function refreshData() {
    refreshButton.disabled = true;
    const results = await Promise.allSettled([
        loadLojas(), loadCarros(), loadClientes(), loadAlugueis()
    ]);
    if (results[0].status === "rejected") {
        lojas = [];
        document.querySelector("#total-lojas").textContent = "—";
        showTableMessage(document.querySelector("#lojas-tabela"), 3, results[0].reason.message);
        lojaSelect.replaceChildren(new Option("Não foi possível carregar as lojas", ""));
        carroSubmit.disabled = true;
    }
    if (results[1].status === "rejected") {
        carros = [];
        showTableMessage(tabela, 6, results[1].reason.message);
        document.querySelector("#total-carros").textContent = "—";
        document.querySelector("#carros-disponiveis").textContent = "—";
        aluguelCarroSelect.replaceChildren(new Option("Não foi possível carregar os carros", ""));
    }
    if (results[2].status === "rejected") {
        clientes = [];
        showTableMessage(document.querySelector("#clientes-tabela"), 4, results[2].reason.message);
    }
    if (results[3].status === "rejected") {
        alugueis = [];
        showTableMessage(document.querySelector("#alugueis-tabela"), 8, results[3].reason.message);
    }
    document.querySelector("#aluguel-submit").disabled =
        !carros.some(carro => carro.statusCarro === "DISPONIVEL") || clientes.length === 0;
    refreshButton.disabled = false;
}

function editarLoja(loja) {
    lojaEditId = loja.id;
    lojaForm.elements.namedItem("cidade").value = loja.cidade;
    lojaForm.elements.namedItem("endereco").value = loja.endereco;
    document.querySelector("#loja-submit").textContent = "Salvar alterações";
    document.querySelector("#loja-cancelar").hidden = false;
    lojaForm.scrollIntoView({ behavior: "smooth", block: "start" });
}

function cancelarLoja() {
    lojaEditId = null;
    lojaForm.reset();
    document.querySelector("#loja-submit").textContent = "Cadastrar loja";
    document.querySelector("#loja-cancelar").hidden = true;
}

function editarCarro(carro) {
    carroEditId = carro.id;
    carroForm.elements.namedItem("modelo").value = carro.modelo;
    carroForm.elements.namedItem("categoria").value = carro.categoria;
    carroForm.elements.namedItem("dataFabricacao").value = carro.dataFabricacao;
    carroForm.elements.namedItem("preco").value = carro.precoFixo;
    lojaSelect.value = carro.idLoja;
    document.querySelector("#carro-submit").textContent = "Salvar alterações";
    document.querySelector("#carro-cancelar").hidden = false;
    carroForm.scrollIntoView({ behavior: "smooth", block: "start" });
}

function cancelarCarro() {
    carroEditId = null;
    carroForm.reset();
    document.querySelector("#carro-submit").textContent = "Cadastrar carro";
    document.querySelector("#carro-cancelar").hidden = true;
}

function editarCliente(cliente) {
    clienteEditId = cliente.id;
    clienteForm.elements.namedItem("nome").value = cliente.nome;
    clienteForm.elements.namedItem("cpf").value = formatCpf(cliente.cpf);
    clienteForm.elements.namedItem("telefone").value = cliente.telefone;
    document.querySelector("#cliente-submit").textContent = "Salvar alterações";
    document.querySelector("#cliente-cancelar").hidden = false;
    clienteForm.scrollIntoView({ behavior: "smooth", block: "start" });
}

function cancelarCliente() {
    clienteEditId = null;
    clienteForm.reset();
    document.querySelector("#cliente-submit").textContent = "Cadastrar cliente";
    document.querySelector("#cliente-cancelar").hidden = true;
}

async function excluirRecurso(url, nome, feedbackId) {
    if (!window.confirm(`Deseja excluir este ${nome}?`)) return;
    try {
        await requestJson(url, { method: "DELETE" });
        await refreshData();
        showFeedback(feedbackId, `${nome[0].toUpperCase() + nome.slice(1)} excluído com sucesso.`, "success");
    } catch (error) {
        showFeedback(feedbackId, error.message, "error");
    }
}

async function devolverAluguel(aluguel) {
    if (!window.confirm(`Registrar devolução do carro ${aluguel.modeloCarro}?`)) return;
    try {
        await requestJson(`/alugueis/${aluguel.id}/devolucao`, { method: "POST" });
        await refreshData();
        showFeedback("aluguel-feedback", "Devolução registrada. O carro está disponível.", "success");
    } catch (error) {
        showFeedback("aluguel-feedback", error.message, "error");
    }
}

function atualizarTotal() {
    const carro = carros.find(item => item.id === aluguelCarroSelect.value);
    const dias = Number(aluguelForm.elements.namedItem("tempo").value);
    document.querySelector("#aluguel-total").textContent = carro && Number.isInteger(dias) && dias > 0
        ? `Total previsto: ${currency.format(carro.precoFixo * dias)}`
        : "Selecione um carro e informe a quantidade de dias.";
}

lojaForm.addEventListener("submit", async event => {
    event.preventDefault();
    const submit = document.querySelector("#loja-submit");
    submit.disabled = true;
    try {
        await requestJson(lojaEditId ? `/loja/${lojaEditId}` : "/loja", {
            method: lojaEditId ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                cidade: lojaForm.elements.namedItem("cidade").value.trim(),
                endereco: lojaForm.elements.namedItem("endereco").value.trim()
            })
        });
        cancelarLoja();
        await refreshData();
        showFeedback("loja-feedback", "Loja salva com sucesso.", "success");
    } catch (error) {
        showFeedback("loja-feedback", error.message, "error");
    } finally {
        submit.disabled = false;
    }
});

carroForm.addEventListener("submit", async event => {
    event.preventDefault();
    const submit = document.querySelector("#carro-submit");
    submit.disabled = true;
    try {
        await requestJson(carroEditId ? `/carros/${carroEditId}` : "/carros", {
            method: carroEditId ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                modelo: carroForm.elements.namedItem("modelo").value.trim(),
                categoria: carroForm.elements.namedItem("categoria").value,
                dataFabricacao: carroForm.elements.namedItem("dataFabricacao").value,
                precoFixo: Number(carroForm.elements.namedItem("preco").value),
                idLoja: lojaSelect.value
            })
        });
        cancelarCarro();
        await refreshData();
        showFeedback("carro-feedback", "Carro salvo com sucesso.", "success");
    } catch (error) {
        showFeedback("carro-feedback", error.message, "error");
    } finally {
        submit.disabled = lojas.length === 0;
    }
});

clienteForm.addEventListener("submit", async event => {
    event.preventDefault();
    const submit = document.querySelector("#cliente-submit");
    submit.disabled = true;
    try {
        await requestJson(clienteEditId ? `/cliente/${clienteEditId}` : "/cliente", {
            method: clienteEditId ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                nome: clienteForm.elements.namedItem("nome").value.trim(),
                cpf: clienteForm.elements.namedItem("cpf").value.trim(),
                telefone: clienteForm.elements.namedItem("telefone").value.trim()
            })
        });
        cancelarCliente();
        await refreshData();
        showFeedback("cliente-feedback", "Cliente salvo com sucesso.", "success");
    } catch (error) {
        showFeedback("cliente-feedback", error.message, "error");
    } finally {
        submit.disabled = false;
    }
});

aluguelForm.addEventListener("submit", async event => {
    event.preventDefault();
    const submit = document.querySelector("#aluguel-submit");
    submit.disabled = true;
    try {
        await requestJson("/alugueis", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                idCarro: aluguelCarroSelect.value,
                cpfCliente: aluguelForm.elements.namedItem("cpfCliente").value.trim(),
                tempo: Number(aluguelForm.elements.namedItem("tempo").value)
            })
        });
        aluguelForm.reset();
        await refreshData();
        showFeedback("aluguel-feedback", "Aluguel registrado com sucesso.", "success");
    } catch (error) {
        showFeedback("aluguel-feedback", error.message, "error");
    } finally {
        submit.disabled = !carros.some(carro => carro.statusCarro === "DISPONIVEL") || clientes.length === 0;
    }
});

document.querySelector("#loja-cancelar").addEventListener("click", cancelarLoja);
document.querySelector("#carro-cancelar").addEventListener("click", cancelarCarro);
document.querySelector("#cliente-cancelar").addEventListener("click", cancelarCliente);
aluguelCarroSelect.addEventListener("change", atualizarTotal);
aluguelForm.elements.namedItem("tempo").addEventListener("input", atualizarTotal);
refreshButton.addEventListener("click", refreshData);
function mostrarPainelSelecionado() {
    const paineis = [...document.querySelectorAll("[data-panel]")];
    const painelSelecionado = paineis.find(painel => `#${painel.id}` === window.location.hash)
        || document.querySelector("#inicio");

    paineis.forEach(painel => {
        painel.hidden = painel !== painelSelecionado;
    });
    document.querySelectorAll(".nav-link").forEach(link => {
        const selecionado = link.getAttribute("href") === `#${painelSelecionado.id}`;
        link.classList.toggle("active", selecionado);
        if (selecionado) {
            link.setAttribute("aria-current", "page");
        } else {
            link.removeAttribute("aria-current");
        }
    });
    window.scrollTo(0, 0);
}

window.addEventListener("hashchange", mostrarPainelSelecionado);
mostrarPainelSelecionado();
refreshData();
