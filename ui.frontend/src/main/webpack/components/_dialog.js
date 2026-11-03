(function (document) {
  "use strict";

  var MULTIFIELD_SELECTOR = ".skills-multifield";
  var MAX_ITEMS = 4;

  function updateMultifield(multifield) {
    var items = multifield.items ? multifield.items.getAll() : [];

    var addButton = multifield.querySelector("button[coral-multifield-add]");

    if (addButton) {
      addButton.disabled = items.length >= MAX_ITEMS;
    }
  }

  function initializeMultifields() {
    var multifields = document.querySelectorAll(MULTIFIELD_SELECTOR);

    multifields.forEach(function (multifield) {
      if (multifield.dataset.limitInitialized) {
        return;
      }

      multifield.dataset.limitInitialized = "true";

      multifield.addEventListener("coral-collection:add", function () {
        updateMultifield(multifield);
      });

      multifield.addEventListener("coral-collection:remove", function () {
        updateMultifield(multifield);
      });

      multifield.addEventListener("coral-component:attached", function () {
        updateMultifield(multifield);
      });

      Coral.commons.ready(multifield, function () {
        updateMultifield(multifield);
      });
    });
  }

  function start() {
    initializeMultifields();

    var observer = new MutationObserver(function () {
      initializeMultifields();
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true,
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", start);
  } else {
    start();
  }
})(document);
