(function ($, document) {
  "use strict";

  function updateCssField() {
    var checkbox = document.querySelector('.cq-dialog [name="./enablePII"]');

    var cssField = document.querySelector('.cq-dialog [name="./cssClass"]');

    if (!checkbox || !cssField) {
      return;
    }

    var isEnabled = checkbox.checked;

    // Enable or disable the CSS Class field
    cssField.disabled = !isEnabled;

    // Clear the value when unchecked
    if (!isEnabled) {
      cssField.value = "";
    }

    // Remove the old CSS class property when unchecked
    var form = checkbox.closest("form");

    if (form) {
      var deleteField = form.querySelector('[name="./cssClass@Delete"]');

      if (!isEnabled) {
        if (!deleteField) {
          deleteField = document.createElement("input");
          deleteField.type = "hidden";
          deleteField.name = "./cssClass@Delete";
          deleteField.value = "true";
          form.appendChild(deleteField);
        }
      } else if (deleteField) {
        deleteField.remove();
      }
    }
  }

  // Run when the dialog opens
  $(document).on("dialog-ready", function () {
    updateCssField();
  });

  // Run whenever the checkbox changes
  $(document).on("change", '.cq-dialog [name="./enablePII"]', function () {
    updateCssField();
  });
})(Granite.$, document);
