(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('specification-form');
        const container = document.getElementById('specification-blocks-container');
        const template = document.getElementById('specification-block-template');

        function initEditor(textarea) {
            $(textarea).summernote({
                height: 240,
                disableDragAndDrop: true,
                toolbar: [
                    ['textStyle', ['normalText', 'specBold', 'specItalic', 'specUnderline']],
                    ['lists', ['specUl', 'specOl']],
                    ['align', ['specAlignLeft', 'specAlignRight']]
                ],
                buttons: {
                    normalText: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-btn',
                            contents: 'Обычный текст',
                            tooltip: 'Обычный текст',
                            click: function () {
                                context.invoke('editor.removeFormat');
                            }
                        }).render();
                    },

                    specBold: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-btn',
                            contents: '<strong>Полужирный</strong>',
                            tooltip: 'Полужирный',
                            click: function () {
                                context.invoke('editor.bold');
                            }
                        }).render();
                    },

                    specItalic: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-btn',
                            contents: '<em>Курсив</em>',
                            tooltip: 'Курсив',
                            click: function () {
                                context.invoke('editor.italic');
                            }
                        }).render();
                    },

                    specUnderline: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-btn',
                            contents: '<span style="text-decoration: underline;">Подчёркнутый</span>',
                            tooltip: 'Подчёркнутый',
                            click: function () {
                                context.invoke('editor.underline');
                            }
                        }).render();
                    },

                    specUl: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-icon-btn',
                            contents: '<i class="fas fa-list-ul"></i>',
                            tooltip: 'Маркированный список',
                            click: function () {
                                context.invoke('editor.insertUnorderedList');
                            }
                        }).render();
                    },

                    specOl: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-icon-btn',
                            contents: '<i class="fas fa-list-ol"></i>',
                            tooltip: 'Нумерованный список',
                            click: function () {
                                context.invoke('editor.insertOrderedList');
                            }
                        }).render();
                    },

                    specAlignLeft: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-icon-btn',
                            contents: '<i class="fas fa-align-left"></i>',
                            tooltip: 'Выровнять по левому краю',
                            click: function () {
                                context.invoke('editor.justifyLeft');
                            }
                        }).render();
                    },

                    specAlignRight: function (context) {
                        const ui = $.summernote.ui;

                        return ui.button({
                            className: 'nb-spec-toolbar-icon-btn',
                            contents: '<i class="fas fa-align-right"></i>',
                            tooltip: 'Выровнять по правому краю',
                            click: function () {
                                context.invoke('editor.justifyRight');
                            }
                        }).render();
                    }
                }
            });
        }

        function destroyEditor(textarea) {
            if (textarea && $(textarea).next('.note-editor').length) {
                $(textarea).summernote('destroy');
            }
        }

        function renumberBlocks() {
            const items = container.querySelectorAll('.js-specification-block');

            items.forEach(function (item, index) {
                const label = item.querySelector('.nb-block-title');
                const sortOrderInput = item.querySelector('[data-field="sortOrder"]');
                const textarea = item.querySelector('[data-field="content"]');
                const actionButton = item.querySelector('.js-specification-action');

                if (label) {
                    label.textContent = 'Слайд ' + (index + 1);
                }

                if (sortOrderInput) {
                    sortOrderInput.name = 'blocks[' + index + '].sortOrder';
                    sortOrderInput.id = 'blocks' + index + '.sortOrder';
                    sortOrderInput.value = index + 1;
                }

                if (textarea) {
                    textarea.name = 'blocks[' + index + '].content';
                    textarea.id = 'blocks' + index + '.content';
                }

                if (actionButton) {
                    if (index === 0) {
                        actionButton.dataset.action = 'add';
                        actionButton.className = 'btn btn-sm btn-outline-secondary js-specification-action';
                        actionButton.innerHTML = '<i class="fas fa-plus"></i>';
                    } else {
                        actionButton.dataset.action = 'remove';
                        actionButton.className = 'btn btn-sm btn-danger js-specification-action';
                        actionButton.innerHTML = '<i class="fas fa-trash"></i>';
                    }
                }
            });
        }

        function addBlock() {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = template.innerHTML.trim();

            const newBlock = wrapper.firstElementChild;
            container.appendChild(newBlock);

            renumberBlocks();

            const textarea = newBlock.querySelector('.js-specification-editor');
            if (textarea) {
                initEditor(textarea);
            }
        }

        function ensureAtLeastOneBlock() {
            const items = container.querySelectorAll('.js-specification-block');

            if (items.length === 0) {
                addBlock();
                return;
            }

            renumberBlocks();

            items.forEach(function (item) {
                const textarea = item.querySelector('.js-specification-editor');
                if (textarea) {
                    initEditor(textarea);
                }
            });
        }

        if (container && form && template) {
            ensureAtLeastOneBlock();

            container.addEventListener('click', function (event) {
                const actionButton = event.target.closest('.js-specification-action');
                if (!actionButton) {
                    return;
                }

                const action = actionButton.dataset.action;

                if (action === 'add') {
                    addBlock();
                    return;
                }

                if (action === 'remove') {
                    const item = actionButton.closest('.js-specification-block');
                    if (item) {
                        const textarea = item.querySelector('.js-specification-editor');
                        destroyEditor(textarea);
                        item.remove();
                        renumberBlocks();
                    }
                }
            });

            form.addEventListener('submit', function () {
                container.querySelectorAll('.js-specification-editor').forEach(function (textarea) {
                    if ($(textarea).next('.note-editor').length) {
                        textarea.value = $(textarea).summernote('code');
                    }
                });

                renumberBlocks();
            });
        }
    });

})();
