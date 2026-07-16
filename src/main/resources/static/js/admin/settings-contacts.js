(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var form = document.getElementById('contacts-form');
        var container = document.getElementById('recipient-emails-container');
        var addButton = document.getElementById('add-recipient-email-btn');
        var template = document.getElementById('recipient-email-template');
        var emptyState = document.getElementById('recipient-emails-empty-state');

        if (!container || !template) {
            return;
        }

        function syncEmptyState() {
            var visibleRows = container.querySelectorAll('.recipient-email-row:not(.d-none)');

            if (emptyState) {
                emptyState.classList.toggle('d-none', visibleRows.length > 0);
            }
        }

        function updateRowVisualState(row) {
            var isActiveInput = row.querySelector('[data-field="isActive"]');
            var badge = row.querySelector('.recipient-status-badge');
            var toggleBtn = row.querySelector('.recipient-toggle-btn');
            var toggleIcon = toggleBtn ? toggleBtn.querySelector('i') : null;
            var deleteBtn = row.querySelector('.recipient-delete-btn');

            if (!isActiveInput) {
                return;
            }

            var isActive = isActiveInput.value === 'true';

            if (badge) {
                badge.classList.remove('badge-success', 'badge-secondary');
                badge.classList.add(isActive ? 'badge-success' : 'badge-secondary');
                badge.textContent = isActive ? 'Активен' : 'Отключен';
            }

            if (toggleIcon) {
                toggleIcon.classList.remove('fa-plus', 'fa-minus');
                toggleIcon.classList.add(isActive ? 'fa-minus' : 'fa-plus');
            }

            if (deleteBtn) {
                deleteBtn.classList.toggle('d-none', isActive);
            }
        }

        function renumberRows() {
            var rows = container.querySelectorAll('.recipient-email-row');

            rows.forEach(function (row, index) {
                var idInput = row.querySelector('[data-field="id"]');
                var emailInput = row.querySelector('[data-field="email"]');
                var isActiveInput = row.querySelector('[data-field="isActive"]');
                var markedForDeleteInput = row.querySelector('[data-field="markedForDelete"]');

                if (idInput) {
                    idInput.name = 'recipientEmails[' + index + '].id';
                    idInput.id = 'recipientEmails' + index + '.id';
                }

                if (emailInput) {
                    emailInput.name = 'recipientEmails[' + index + '].email';
                    emailInput.id = 'recipientEmails' + index + '.email';
                }

                if (isActiveInput) {
                    isActiveInput.name = 'recipientEmails[' + index + '].isActive';
                    isActiveInput.id = 'recipientEmails' + index + '.isActive';
                }

                if (markedForDeleteInput) {
                    markedForDeleteInput.name = 'recipientEmails[' + index + '].markedForDelete';
                    markedForDeleteInput.id = 'recipientEmails' + index + '.markedForDelete';
                }
            });
        }

        function addRecipientEmailRow() {
            var wrapper = document.createElement('div');
            wrapper.innerHTML = template.innerHTML.trim();

            var newRow = wrapper.firstElementChild;
            container.appendChild(newRow);

            renumberRows();
            updateRowVisualState(newRow);
            syncEmptyState();
        }

        if (addButton) {
            addButton.addEventListener('click', function () {
                addRecipientEmailRow();
            });
        }

        container.addEventListener('click', function (event) {
            var toggleBtn = event.target.closest('.recipient-toggle-btn');
            if (toggleBtn) {
                var row = toggleBtn.closest('.recipient-email-row');
                var isActiveInput = row.querySelector('[data-field="isActive"]');

                var isActive = isActiveInput.value === 'true';
                isActiveInput.value = isActive ? 'false' : 'true';

                updateRowVisualState(row);
                return;
            }

            var deleteBtn = event.target.closest('.recipient-delete-btn');
            if (deleteBtn) {
                var row = deleteBtn.closest('.recipient-email-row');
                var idInput = row.querySelector('[data-field="id"]');
                var markedForDeleteInput = row.querySelector('[data-field="markedForDelete"]');

                var hasId = idInput && idInput.value && idInput.value.trim() !== '';

                if (!hasId) {
                    row.remove();
                } else if (markedForDeleteInput) {
                    markedForDeleteInput.value = 'true';
                    row.classList.add('d-none');
                }

                renumberRows();
                syncEmptyState();
            }
        });

        var existingRows = container.querySelectorAll('.recipient-email-row');
        existingRows.forEach(function (row) {
            updateRowVisualState(row);
        });

        renumberRows();
        syncEmptyState();

        if (form) {
            form.addEventListener('submit', function () {
                renumberRows();
            });
        }
    });
})();
