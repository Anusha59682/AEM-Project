(function (document, $) {
  "use strict";

  $(document).on("dialog-ready", function () {
    var dropdown = document.querySelector(
      "coral-select.profiles-image-dropdown",
    );

    if (!dropdown || dropdown.dataset.loading === "true") {
      return;
    }

    dropdown.dataset.loading = "true";

    // Wait until Coral Select is ready.
    Coral.commons.ready(dropdown, function () {
      // Store the value already loaded by the dialog.
      var selectedValue = dropdown.value || "";

      $.ajax({
        url: "/bin/demo/profiles",
        type: "GET",
        dataType: "json",

        success: function (images) {
          // Remove old dropdown options.
          dropdown.items.clear();

          // Add default option.
          var defaultOption = document.createElement("coral-select-item");

          defaultOption.value = "";
          defaultOption.textContent = "Select an image";

          dropdown.items.add(defaultOption);

          // Add DAM images.
          images.forEach(function (image) {
            var option = document.createElement("coral-select-item");

            option.value = image.path;
            option.textContent = image.name;

            dropdown.items.add(option);
          });

          // Restore saved selection if it exists.
          var imageExists = images.some(function (image) {
            return image.path === selectedValue;
          });

          dropdown.value = imageExists ? selectedValue : "";

          dropdown.dataset.loading = "false";

          console.log("Profiles images loaded:", images.length);

          console.log("Selected image:", dropdown.value);
        },

        error: function (xhr, status, error) {
          dropdown.dataset.loading = "false";

          console.error("Unable to load profile images:", error);
        },
      });
    });
  });
})(document, Granite.$);
