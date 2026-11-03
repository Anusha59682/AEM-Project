(function (document, $) {
  "use strict";

  // ==========================================
  // 1. DYNAMIC FIELDS - DROPDOWN
  // ==========================================

  var dialogSelector = ".experienceconfig-dynamic-fields";
  var dropdownSelector = '[name="./fieldCount"]';
  var groupSelector = ".experienceconfig-option-group";

  function updateFields(dialog) {
    var dropdown = dialog.querySelector(dropdownSelector);

    if (!dropdown) {
      return;
    }

    var selectedValue = dropdown.value;
    var groups = dialog.querySelectorAll(groupSelector);

    groups.forEach(function (group) {
      var optionValue = group.getAttribute("data-option-value");

      group.classList.toggle("hide", optionValue !== selectedValue);
    });
  }

  function initializeDialog() {
    document.querySelectorAll(dialogSelector).forEach(function (dialog) {
      updateFields(dialog);
    });
  }

  $(document).on(
    "change",
    dialogSelector + " " + dropdownSelector,
    function () {
      var dialog = this.closest(dialogSelector);

      if (dialog) {
        updateFields(dialog);
      }
    },
  );

  $(document).on("foundation-contentloaded", function () {
    initializeDialog();
  });

  // ==========================================
  // 2. ADDITIONAL INFO - CHECKBOX
  // ==========================================

  function updateAdditionalInfo(dialog) {
    var checkbox = dialog.querySelector(".experienceconfig-enable-additional");

    var additionalField = dialog.querySelector(
      ".experienceconfig-additional-field",
    );

    if (!checkbox || !additionalField) {
      return;
    }

    additionalField.classList.toggle("hide", !checkbox.checked);
  }

  function initializeAdditionalInfo() {
    document
      .querySelectorAll(".experienceconfig-enable-additional")
      .forEach(function (checkbox) {
        var dialog = checkbox.closest("form") || checkbox.closest(".cq-dialog");

        if (dialog) {
          updateAdditionalInfo(dialog);
        }
      });
  }

  $(document).on("change", ".experienceconfig-enable-additional", function () {
    var dialog = this.closest("form") || this.closest(".cq-dialog");

    if (dialog) {
      updateAdditionalInfo(dialog);
    }
  });

  $(document).on("foundation-contentloaded", function () {
    initializeAdditionalInfo();
  });

  // ==========================================
  // 3. PREVIOUS EXPERIENCE - MAXIMUM 4 ENTRIES
  // ==========================================

  var previousExperienceSelector = ".experienceconfig-previous-experience";

  function getPreviousExperienceMultifield(element) {
    if (!element) {
      return null;
    }

    var multifield = element.closest("coral-multifield");

    if (
      multifield &&
      (multifield.matches(previousExperienceSelector) ||
        multifield.closest(previousExperienceSelector))
    ) {
      return multifield;
    }

    var container = element.closest(previousExperienceSelector);

    if (!container) {
      return null;
    }

    if (container.matches("coral-multifield")) {
      return container;
    }

    return container.querySelector("coral-multifield");
  }

  function getPreviousExperienceCount(multifield) {
    return multifield.querySelectorAll(":scope > coral-multifield-item").length;
  }

  function getPreviousExperienceError(multifield) {
    var parent = multifield.parentElement;

    if (!parent) {
      return null;
    }

    return parent.querySelector(".experienceconfig-max-error");
  }

  function showPreviousExperienceError(multifield) {
    var error = getPreviousExperienceError(multifield);

    if (!error) {
      error = document.createElement("div");
      error.className = "experienceconfig-max-error coral-Form-error";
      error.setAttribute("role", "alert");
      error.style.color = "#c9252d";
      error.style.marginTop = "8px";

      multifield.insertAdjacentElement("afterend", error);
    }

    error.textContent = "You have reached the maximum number of fields (4).";
  }

  function clearPreviousExperienceError(multifield) {
    var error = getPreviousExperienceError(multifield);

    if (error) {
      error.remove();
    }
  }

  function refreshPreviousExperienceError(multifield) {
    if (!multifield) {
      return;
    }

    var count = getPreviousExperienceCount(multifield);

    if (count < 4) {
      clearPreviousExperienceError(multifield);
    }
  }

  // Intercept attempts to add a fifth entry.
  document.addEventListener(
    "click",
    function (event) {
      var target = event.target;

      if (!(target instanceof Element)) {
        return;
      }

      var addButton = target.closest("[coral-multifield-add]");

      if (!addButton) {
        return;
      }

      var multifield = getPreviousExperienceMultifield(addButton);

      if (!multifield) {
        return;
      }

      if (getPreviousExperienceCount(multifield) >= 4) {
        event.preventDefault();
        event.stopImmediatePropagation();

        showPreviousExperienceError(multifield);
      }
    },
    true,
  );

  // Listen for Coral UI item removal.
  document.addEventListener("coral-multifield:itemremoved", function (event) {
    var multifield = event.target;

    if (!(multifield instanceof Element)) {
      return;
    }

    multifield = getPreviousExperienceMultifield(multifield);

    if (multifield) {
      refreshPreviousExperienceError(multifield);
    }
  });

  // Also monitor removal clicks as a fallback.
  // Check the count after Coral UI updates the multifield.
  document.addEventListener(
    "click",
    function (event) {
      var target = event.target;

      if (!(target instanceof Element)) {
        return;
      }

      var removeButton = target.closest("[coral-multifield-remove]");

      if (!removeButton) {
        return;
      }

      var multifield = getPreviousExperienceMultifield(removeButton);

      if (!multifield) {
        return;
      }

      window.requestAnimationFrame(function () {
        window.requestAnimationFrame(function () {
          refreshPreviousExperienceError(multifield);
        });
      });
    },
    true,
  );
})(document, Granite.$);
