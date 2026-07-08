(function () {
    'use strict';

    document.addEventListener('submit', function (event) {
        var form = event.target;

        if (!form.matches('[data-confirm]')) {
            return;
        }

        var message = form.getAttribute('data-confirm');

        if (!window.confirm(message)) {
            event.preventDefault();
        }
    });
})();
