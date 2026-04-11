// ============================================
// MICROBANK ADMIN DASHBOARD - JAVASCRIPT
// ============================================

const API_BASE_URL = '/api/v1';

// ============================================
// AUTH — verificar token al cargar
// ============================================
const token = localStorage.getItem('mb_token');
const currentUsername = localStorage.getItem('mb_username');

if (!token) {
    window.location.href = '/login.html';
}

// Headers con JWT para todas las requests autenticadas
function authHeaders(extra = {}) {
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...extra
    };
}

function logout() {
    localStorage.removeItem('mb_token');
    localStorage.removeItem('mb_username');
    localStorage.removeItem('mb_userId');
    window.location.href = '/login.html';
}

// State
let allAccounts = [];
let currentAccount = null;
let operationAccountId = null;

// DOM Elements
const accountsTableBody = document.getElementById('accountsTableBody');
const searchInput = document.getElementById('searchInput');
const createAccountBtn = document.getElementById('createAccountBtn');

const createAccountModal = new bootstrap.Modal(document.getElementById('createAccountModal'));
const transactionsModal = new bootstrap.Modal(document.getElementById('transactionsModal'));
const updateStatusModal = new bootstrap.Modal(document.getElementById('updateStatusModal'));
const depositModal = new bootstrap.Modal(document.getElementById('depositModal'));
const withdrawModal = new bootstrap.Modal(document.getElementById('withdrawModal'));
const transferModal = new bootstrap.Modal(document.getElementById('transferModal'));

const createAccountForm = document.getElementById('createAccountForm');
const submitCreateBtn = document.getElementById('submitCreateBtn');
const newStatusSelect = document.getElementById('newStatus');
const submitStatusBtn = document.getElementById('submitStatusBtn');
const submitDepositBtn = document.getElementById('submitDepositBtn');
const submitWithdrawBtn = document.getElementById('submitWithdrawBtn');
const submitTransferBtn = document.getElementById('submitTransferBtn');

// ============================================
// INITIALIZATION
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    setCurrentDate();
    loadAccounts();
    attachEventListeners();
    // Mostrar usuario autenticado en el navbar
    const userEl = document.getElementById('currentUser');
    if (userEl && currentUsername) userEl.textContent = currentUsername;
});

function setCurrentDate() {
    const today = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('currentDate').textContent = today.toLocaleDateString('es-ES', options);
}

function attachEventListeners() {
    createAccountBtn.addEventListener('click', () => {
        resetCreateForm();
        createAccountModal.show();
    });

    submitCreateBtn.addEventListener('click', handleCreateAccount);

    searchInput.addEventListener('input', (e) => {
        filterAccounts(e.target.value.toLowerCase());
    });

    submitStatusBtn.addEventListener('click', handleUpdateStatus);
    newStatusSelect.addEventListener('change', showStatusWarning);

    submitDepositBtn.addEventListener('click', handleDeposit);
    submitWithdrawBtn.addEventListener('click', handleWithdraw);
    submitTransferBtn.addEventListener('click', handleTransfer);
}

// ============================================
// LOAD & DISPLAY ACCOUNTS
// ============================================

async function loadAccounts() {
    try {
        showTableLoading();
        const response = await fetch(`${API_BASE_URL}/accounts`, {
            headers: authHeaders()
        });

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) logout();
            throw new Error(`HTTP Error: ${response.status}`);
        }

        const data = await response.json();
        allAccounts = data.data || [];

        renderAccounts(allAccounts);
        updateStats();
    } catch (error) {
        console.error('Error loading accounts:', error);
        showToast('Error al cargar las cuentas. Por favor, intente nuevamente.', 'error');
        showTableLoading(true);
    }
}

function showTableLoading(error = false) {
    const tableBody = document.getElementById('accountsTableBody');
    if (error) {
        tableBody.innerHTML = `
            <tr><td colspan="6" class="text-center py-5">
                <span style="font-size: 2rem; opacity: 0.4;">⚠️</span>
                <p class="text-muted mt-2">Error al cargar las cuentas. Por favor, actualice la página.</p>
            </td></tr>`;
    } else {
        tableBody.innerHTML = `
            <tr class="loading-row"><td colspan="6" class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
            </td></tr>`;
    }
}

function renderAccounts(accounts) {
    const tableBody = document.getElementById('accountsTableBody');
    const noResultsSection = document.getElementById('noResultsSection');

    if (accounts.length === 0) {
        tableBody.innerHTML = '';
        noResultsSection.style.display = 'block';
        return;
    }

    noResultsSection.style.display = 'none';
    tableBody.innerHTML = accounts.map(account => createAccountRow(account)).join('');

    tableBody.querySelectorAll('.btn-view-transactions').forEach(btn => {
        btn.addEventListener('click', () => loadTransactions(btn.dataset.accountId));
    });

    tableBody.querySelectorAll('.btn-update-status').forEach(btn => {
        btn.addEventListener('click', () => prepareUpdateStatus(btn.dataset.accountId));
    });

    tableBody.querySelectorAll('.btn-deposit').forEach(btn => {
        btn.addEventListener('click', () => prepareDeposit(btn.dataset.accountId));
    });

    tableBody.querySelectorAll('.btn-withdraw').forEach(btn => {
        btn.addEventListener('click', () => prepareWithdraw(btn.dataset.accountId));
    });

    tableBody.querySelectorAll('.btn-transfer').forEach(btn => {
        btn.addEventListener('click', () => prepareTransfer(btn.dataset.accountId));
    });
}

function createAccountRow(account) {
    const accountNumber = account.accountNumber || 'N/A';
    const accountType = account.accountType || 'N/A';
    const balance = formatCurrency(account.balance || 0);
    const status = account.status || 'N/A';
    const createdAt = formatDate(account.createdAt);
    const statusBadgeClass = getStatusBadgeClass(status);
    const aliasDisplay = account.alias
        ? `<br><small class="text-muted">@${escapeHtml(account.alias)}</small>`
        : `<br><small class="text-muted opacity-50" style="cursor:pointer" onclick="promptSetAlias('${account.id}')">+ alias</small>`;

    return `
        <tr>
            <td class="account-number">${escapeHtml(accountNumber)}${aliasDisplay}</td>
            <td>${escapeHtml(accountType)}</td>
            <td class="account-balance">${balance}</td>
            <td><span class="badge ${statusBadgeClass}">${escapeHtml(status)}</span></td>
            <td class="transaction-date">${createdAt}</td>
            <td class="actions-col">
                <div class="action-buttons">
                    <button class="btn btn-action btn-sm btn-deposit" data-account-id="${account.id}" title="Depositar">💰</button>
                    <button class="btn btn-action btn-sm btn-withdraw" data-account-id="${account.id}" title="Retirar">💸</button>
                    <button class="btn btn-action btn-sm btn-transfer" data-account-id="${account.id}" title="Transferir">🔄</button>
                    <button class="btn btn-action btn-sm btn-view-transactions" data-account-id="${account.id}" title="Ver Transacciones">📊</button>
                    <button class="btn btn-action btn-sm btn-update-status" data-account-id="${account.id}" title="Actualizar Estado">⚙️</button>
                </div>
            </td>
        </tr>
    `;
}

async function promptSetAlias(accountId) {
    const alias = prompt('Ingresá un alias para esta cuenta\n(solo minúsculas, números y guiones, ej: mi-ahorro):');
    if (!alias) return;
    try {
        const res = await fetch(`${API_BASE_URL}/accounts/${accountId}/alias`, {
            method: 'PUT',
            headers: authHeaders(),
            body: JSON.stringify({ alias: alias.toLowerCase().trim() })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Error al asignar alias');
        showToast(`Alias "@${alias}" asignado correctamente`, 'success');
        loadAccounts();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ============================================
// CREATE ACCOUNT
// ============================================

async function handleCreateAccount() {
    const accountType = document.getElementById('accountType').value;
    const initialBalance = parseFloat(document.getElementById('initialBalance').value);

    if (!accountType) {
        showFieldError('typeError', 'Por favor seleccione un tipo de cuenta.');
        return;
    }

    if (!initialBalance || initialBalance <= 0) {
        showToast('El saldo inicial debe ser mayor que cero.', 'warning');
        return;
    }

    submitCreateBtn.disabled = true;
    submitCreateBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Creando...';

    try {
        const response = await fetch(`${API_BASE_URL}/accounts`, {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({ accountType, initialBalance })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP Error: ${response.status}`);
        }

        const data = await response.json();
        const newAccount = data.data;

        allAccounts.push(newAccount);
        renderAccounts(allAccounts);
        updateStats();

        createAccountModal.hide();
        showToast(`Cuenta creada: ${newAccount.accountNumber}`, 'success');
        resetCreateForm();
    } catch (error) {
        console.error('Error creating account:', error);
        showToast(`Error: ${error.message}`, 'error');
    } finally {
        submitCreateBtn.disabled = false;
        submitCreateBtn.innerHTML = 'Crear Cuenta';
    }
}

function resetCreateForm() {
    createAccountForm.reset();
    createAccountForm.classList.remove('was-validated');
    document.getElementById('typeError').style.display = 'none';
}

function showFieldError(elementId, message) {
    const errorEl = document.getElementById(elementId);
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
    }
}

// ============================================
// DEPOSIT
// ============================================

function prepareDeposit(accountId) {
    const account = allAccounts.find(a => a.id === accountId);
    if (!account) { showToast('Cuenta no encontrada', 'error'); return; }

    operationAccountId = accountId;
    document.getElementById('depositAccountInfo').textContent =
        `Cuenta ${account.accountNumber} — Saldo: ${formatCurrency(account.balance)}`;
    document.getElementById('depositAmount').value = '';
    depositModal.show();
}

async function handleDeposit() {
    const amount = parseFloat(document.getElementById('depositAmount').value);

    if (!amount || amount <= 0) {
        showToast('Ingrese un monto válido mayor que cero.', 'warning');
        return;
    }

    submitDepositBtn.disabled = true;
    submitDepositBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {
        const response = await fetch(`${API_BASE_URL}/accounts/${operationAccountId}/deposit`, {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({ accountId: operationAccountId, amount })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP Error: ${response.status}`);
        }

        depositModal.hide();
        showToast(`Depósito de ${formatCurrency(amount)} completado exitosamente.`, 'success');
        await loadAccounts();
    } catch (error) {
        console.error('Error en el depósito:', error);
        showToast(`Error: ${error.message}`, 'error');
    } finally {
        submitDepositBtn.disabled = false;
        submitDepositBtn.innerHTML = 'Confirmar Depósito';
    }
}

// ============================================
// WITHDRAW
// ============================================

function prepareWithdraw(accountId) {
    const account = allAccounts.find(a => a.id === accountId);
    if (!account) { showToast('Cuenta no encontrada', 'error'); return; }

    operationAccountId = accountId;
    document.getElementById('withdrawAccountInfo').textContent =
        `Cuenta ${account.accountNumber} — Saldo: ${formatCurrency(account.balance)}`;
    document.getElementById('withdrawAmount').value = '';
    withdrawModal.show();
}

async function handleWithdraw() {
    const amount = parseFloat(document.getElementById('withdrawAmount').value);

    if (!amount || amount <= 0) {
        showToast('Ingrese un monto válido mayor que cero.', 'warning');
        return;
    }

    submitWithdrawBtn.disabled = true;
    submitWithdrawBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {
        const response = await fetch(`${API_BASE_URL}/accounts/${operationAccountId}/withdraw`, {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({ accountId: operationAccountId, amount })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP Error: ${response.status}`);
        }

        withdrawModal.hide();
        showToast(`Retiro de ${formatCurrency(amount)} completado exitosamente.`, 'success');
        await loadAccounts();
    } catch (error) {
        console.error('Error en el retiro:', error);
        showToast(`Error: ${error.message}`, 'error');
    } finally {
        submitWithdrawBtn.disabled = false;
        submitWithdrawBtn.innerHTML = 'Confirmar Retiro';
    }
}

// ============================================
// TRANSFER — LÓGICA CRÍTICA EN FRONTEND (Martín)
// ============================================
// IMPORTANTE: El verdadero locking ACID ocurre en el backend (TransferService).
// El frontend solo:
//   1. Valida que sea != cuenta origen
//   2. Valida que monto sea > 0
//   3. Muestra solo cuentas ACTIVO como destino (seguridad preventiva)
//   4. Envía request al backend
//   5. El backend hace: lock BD → validaciones → transferencia → commit/rollback
// Si una transferencia falla (409 Conflict = lock timeout), el usuario ve error claro.
// El backend NUNCA deja datos corruptos, gracias a @Transactional.

function switchTransferTab(tab, event) {
    document.getElementById('transferTabSelect').style.display = tab === 'select' ? 'block' : 'none';
    document.getElementById('transferTabManual').style.display = tab === 'manual' ? 'block' : 'none';
    document.querySelectorAll('#transferTargetTabs .nav-link').forEach(el => el.classList.remove('active'));
    if (event) event.target.classList.add('active');
}

function prepareTransfer(sourceAccountId) {
    const sourceAccount = allAccounts.find(a => a.id === sourceAccountId);
    if (!sourceAccount) { showToast('Cuenta no encontrada', 'error'); return; }

    operationAccountId = sourceAccountId;

    const aliasLabel = sourceAccount.alias ? ` (@${sourceAccount.alias})` : '';
    document.getElementById('transferSourceInfo').textContent =
        `${sourceAccount.accountNumber}${aliasLabel} — ${formatCurrency(sourceAccount.balance)}`;

    const targetSelect = document.getElementById('transferTargetSelect');
    targetSelect.innerHTML = '<option value="">-- Seleccionar cuenta de destino --</option>';

    // Mostrar mis cuentas ACTIVO (excluyendo la origen)
    allAccounts
        .filter(a => a.id !== sourceAccountId && a.status === 'ACTIVO')
        .forEach(a => {
            const option = document.createElement('option');
            option.value = a.id;
            const alias = a.alias ? ` @${a.alias}` : '';
            option.textContent = `${a.accountNumber}${alias} (${a.accountType}) — ${formatCurrency(a.balance)}`;
            targetSelect.appendChild(option);
        });

    document.getElementById('transferAmount').value = '';
    document.getElementById('transferTargetIdentifier').value = '';
    switchTransferTab('select', null);
    document.querySelectorAll('#transferTargetTabs .nav-link')[0].classList.add('active');
    document.querySelectorAll('#transferTargetTabs .nav-link')[1].classList.remove('active');
    transferModal.show();
}

async function handleTransfer() {
    const amount = parseFloat(document.getElementById('transferAmount').value);

    // Determinar destino: tab select o tab manual
    const manualVisible = document.getElementById('transferTabManual').style.display !== 'none';
    const targetAccountId = manualVisible ? null : document.getElementById('transferTargetSelect').value;
    const targetIdentifier = manualVisible ? document.getElementById('transferTargetIdentifier').value.trim() : null;

    if (!manualVisible && !targetAccountId) {
        showToast('Por favor seleccione una cuenta de destino.', 'warning');
        return;
    }
    if (manualVisible && !targetIdentifier) {
        showToast('Ingrese un número de cuenta o alias.', 'warning');
        return;
    }

    if (!amount || amount <= 0) {
        showToast('Ingrese un monto válido mayor que cero.', 'warning');
        return;
    }

    submitTransferBtn.disabled = true;
    submitTransferBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    const body = { sourceAccountId: operationAccountId, amount };
    if (targetAccountId) body.targetAccountId = targetAccountId;
    if (targetIdentifier) body.targetIdentifier = targetIdentifier;

    try {
        const response = await fetch(`${API_BASE_URL}/transfers`, {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            const errorData = await response.json();
            // 409: Timeout de lock = "Intente nuevamente en un momento"
            // 400: Validación = "Datos incorrectos, revise"
            throw new Error(errorData.error || errorData.message || `HTTP Error: ${response.status}`);
        }

        transferModal.hide();
        showToast(`Transferencia de ${formatCurrency(amount)} completada exitosamente.`, 'success');
        await loadAccounts();
    } catch (error) {
        console.error('Error en la transferencia:', error);
        showToast(`Error: ${error.message}`, 'error');
    } finally {
        submitTransferBtn.disabled = false;
        submitTransferBtn.innerHTML = 'Confirmar Transferencia';
    }
}

// ============================================
// VIEW TRANSACTIONS
// ============================================

async function loadTransactions(accountId) {
    const account = allAccounts.find(a => a.id === accountId);
    if (!account) { showToast('Cuenta no encontrada', 'error'); return; }

    document.getElementById('transactionsLoading').style.display = 'block';
    document.getElementById('transactionsTable').style.display = 'none';
    document.getElementById('noTransactionsMessage').style.display = 'none';
    document.getElementById('transactionsAccountInfo').textContent = `Cuenta #${account.accountNumber}`;

    transactionsModal.show();

    try {
        const response = await fetch(`${API_BASE_URL}/transfers/account/${accountId}`, {
            headers: authHeaders()
        });

        if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);

        const data = await response.json();
        const transactionList = Array.isArray(data) ? data : (data.data || []);

        document.getElementById('transactionsLoading').style.display = 'none';

        if (transactionList.length === 0) {
            document.getElementById('noTransactionsMessage').style.display = 'block';
            return;
        }

        renderTransactions(transactionList);
        document.getElementById('transactionsTable').style.display = 'table';
    } catch (error) {
        console.error('Error al cargar transacciones:', error);
        document.getElementById('transactionsLoading').style.display = 'none';
        showToast(`Error al cargar transacciones: ${error.message}`, 'error');
        transactionsModal.hide();
    }
}

function renderTransactions(transactions) {
    document.getElementById('transactionsTableBody').innerHTML =
        transactions.map(tx => createTransactionRow(tx)).join('');
}

function createTransactionRow(transaction) {
    const type = transaction.type || 'N/A';
    const amount = formatCurrency(transaction.amount || 0);
    const status = transaction.status || 'N/A';
    const date = formatDate(transaction.createdAt);
    const description = transaction.description || '-';

    return `
        <tr>
            <td class="transaction-type">${escapeHtml(type)}</td>
            <td class="transaction-amount">${amount}</td>
            <td><span class="badge ${getStatusBadgeClass(status)}">${escapeHtml(status)}</span></td>
            <td class="transaction-date">${date}</td>
            <td>${escapeHtml(description)}</td>
        </tr>
    `;
}

// ============================================
// UPDATE STATUS
// ============================================

function prepareUpdateStatus(accountId) {
    const account = allAccounts.find(a => a.id === accountId);
    if (!account) { showToast('Cuenta no encontrada', 'error'); return; }

    currentAccount = account;
    operationAccountId = accountId;

    document.getElementById('statusAccountInfo').innerHTML =
        `<strong>${escapeHtml(account.accountNumber)}</strong> — Estado actual: <span class="badge ${getStatusBadgeClass(account.status)}">${escapeHtml(account.status)}</span>`;

    newStatusSelect.value = '';
    document.getElementById('statusWarning').style.display = 'none';

    updateStatusModal.show();
}

function showStatusWarning() {
    const newStatus = newStatusSelect.value;
    const warningEl = document.getElementById('statusWarning');
    const warningText = document.getElementById('warningText');

    if (!newStatus) { warningEl.style.display = 'none'; return; }

    let warning = '';
    if (newStatus === 'SUSPENDIDO') {
        warning = 'Suspender una cuenta bloqueará todas las transacciones.';
    } else if (newStatus === 'CERRADO') {
        warning = 'Cerrar una cuenta es irreversible. Se bloquearán todas las transacciones.';
    }

    if (warning) {
        warningText.textContent = warning;
        warningEl.style.display = 'block';
    } else {
        warningEl.style.display = 'none';
    }
}

async function handleUpdateStatus() {
    if (!newStatusSelect.value) {
        showToast('Por favor seleccione un nuevo estado', 'warning');
        return;
    }

    submitStatusBtn.disabled = true;
    submitStatusBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Actualizando...';

    try {
        const response = await fetch(`${API_BASE_URL}/accounts/${operationAccountId}/status`, {
            method: 'PUT',
            headers: authHeaders(),
            body: JSON.stringify({ newStatus: newStatusSelect.value })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP Error: ${response.status}`);
        }

        const data = await response.json();
        const updatedAccount = data.data;

        const index = allAccounts.findIndex(a => a.id === operationAccountId);
        if (index !== -1) allAccounts[index] = updatedAccount;

        renderAccounts(allAccounts);
        updateStats();

        updateStatusModal.hide();
        showToast(`Estado de la cuenta actualizado a ${updatedAccount.status}`, 'success');
    } catch (error) {
        console.error('Error updating status:', error);
        showToast(`Error: ${error.message}`, 'error');
    } finally {
        submitStatusBtn.disabled = false;
        submitStatusBtn.innerHTML = 'Actualizar Estado';
    }
}

// ============================================
// SEARCH / FILTER
// ============================================

function filterAccounts(query) {
    if (!query) { renderAccounts(allAccounts); return; }

    const filtered = allAccounts.filter(account =>
        (account.accountNumber || '').toLowerCase().includes(query)
    );

    renderAccounts(filtered);
}

// ============================================
// STATS
// ============================================

function updateStats() {
    document.getElementById('totalAccountsCount').textContent = allAccounts.length;
    document.getElementById('activeAccountsCount').textContent =
        allAccounts.filter(a => a.status === 'ACTIVO').length;
    document.getElementById('suspendedAccountsCount').textContent =
        allAccounts.filter(a => a.status === 'SUSPENDIDO').length;
}

// ============================================
// TOAST NOTIFICATIONS
// ============================================

function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer');
    const toastEl = document.createElement('div');
    toastEl.className = `toast toast-${type}`;
    toastEl.setAttribute('role', 'alert');

    const icon = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' }[type] || 'ℹ';

    toastEl.innerHTML = `
        <div class="toast-body">
            <strong style="font-size: 1.1rem; margin-right: 0.5rem;">${icon}</strong>
            ${escapeHtml(message)}
        </div>
    `;

    toastContainer.appendChild(toastEl);
    setTimeout(() => toastEl.remove(), 4000);
}

// ============================================
// UTILITY FUNCTIONS
// ============================================

function formatCurrency(value) {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        return new Date(dateString).toLocaleDateString('es-ES', {
            year: 'numeric', month: 'short', day: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    } catch (e) {
        return dateString;
    }
}

function getStatusBadgeClass(status) {
    const s = (status || '').toUpperCase();
    if (s === 'ACTIVO') return 'badge-active';
    if (s === 'SUSPENDIDO') return 'badge-suspended';
    if (s === 'CERRADO') return 'badge-closed';
    if (s === 'INACTIVO') return 'badge-inactive';
    // transaction statuses
    if (s === 'COMPLETADA') return 'badge-active';
    if (s === 'PENDIENTE') return 'badge-suspended';
    if (s === 'FALLIDA') return 'badge-closed';
    return '';
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

window.addEventListener('error', (event) => console.error('Global error:', event.error));
window.addEventListener('unhandledrejection', (event) => console.error('Unhandled rejection:', event.reason));
