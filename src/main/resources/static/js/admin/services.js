(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        initImageInput({
            inputId: 'bannerImage',
            labelId: 'bannerImageLabel',
            imageId: 'bannerPreview',
            placeholderId: 'bannerPlaceholder'
        });

        initImageInput({
            inputId: 'previewImageInput',
            labelId: 'previewImageLabel',
            imageId: 'previewImage',
            placeholderId: 'previewPlaceholder'
        });
    });

    function initImageInput(config) {
        var input = document.getElementById(config.inputId);
        var label = document.getElementById(config.labelId);
        var image = document.getElementById(config.imageId);
        var placeholder = document.getElementById(config.placeholderId);

        if (!input || !label || !image || !placeholder) {
            return;
        }

        input.addEventListener('change', function (event) {
            var file = event.target.files[0];

            updateFileLabel(label, file);

            if (!file) {
                resetPreview(image, placeholder);
                return;
            }

            if (!file.type || !file.type.startsWith('image/')) {
                resetPreview(image, placeholder);
                return;
            }

            var reader = new FileReader();

            reader.onload = function (loadEvent) {
                image.src = loadEvent.target.result;
                image.classList.remove('d-none');
                placeholder.classList.add('d-none');
            };

            reader.readAsDataURL(file);
        });
    }

    function updateFileLabel(label, file) {
        label.textContent = file ? file.name : 'Выберите файл';
    }

    function resetPreview(image, placeholder) {
        image.src = '';
        image.classList.add('d-none');
        placeholder.classList.remove('d-none');
    }
})();
