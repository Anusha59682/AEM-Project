(function (document, $) {
  "use strict";

  $(document).ready(function () {
    const endpoint = "/bin/demo/contacts";
    const csrfEndpoint = "/libs/granite/csrf/token.json";

    function showMessage(message) {
      $("#contactMessage").text(message);
    }

    function clearForm() {
      $("#contactForm")[0].reset();
      $("#contactId").val("");
      $("#saveButton").text("Submit");
      $("#cancelEdit").prop("hidden", true);
    }

    function getFormData() {
      return {
        id: $("#contactId").val(),
        name: $("#contactName").val().trim(),
        email: $("#contactEmail").val().trim(),
        mobile: $("#contactMobile").val().trim(),
      };
    }

    // Send AJAX request with CSRF token.
    function sendRequest(data) {
      return $.ajax({
        url: endpoint,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        dataType: "json",
      });
    }

    // Create or update contact.
    $("#contactForm").on("submit", function (event) {
      event.preventDefault();

      const data = getFormData();

      if (!/^[0-9]{10}$/.test(data.mobile)) {
        showMessage("Enter a valid 10-digit mobile number.");
        return;
      }

      data.action = data.id ? "update" : "create";

      sendRequest(data)
        .done(function (response) {
          showMessage(response.message || "Contact saved successfully.");
          clearForm();
          window.location.reload();
        })
        .fail(function (xhr) {
          const message =
            xhr.responseJSON?.message ||
            "Unable to save contact. Please try again.";

          showMessage(message);
          console.error("Contact save error:", xhr);
        });
    });

    // Cancel editing.
    $("#cancelEdit").on("click", function (event) {
      event.preventDefault();
      clearForm();
    });

    // Edit selected contact.
    $(document).on("click", ".edit-contact", function () {
      const row = $(this).closest("tr");

      $("#contactId").val(row.attr("data-id"));
      $("#contactName").val(row.attr("data-name"));
      $("#contactEmail").val(row.attr("data-email"));
      $("#contactMobile").val(row.attr("data-mobile"));

      $("#saveButton").text("Update");
      $("#cancelEdit").prop("hidden", false);

      document
        .getElementById("contactForm")
        .scrollIntoView({ behavior: "smooth" });
    });

    // Delete selected contact.
    $(document).on("click", ".delete-contact", function () {
      const id = $(this).closest("tr").attr("data-id");

      if (!id) {
        showMessage("Contact ID not found.");
        return;
      }

      if (!window.confirm("Delete this contact?")) {
        return;
      }

      sendRequest({
        action: "delete",
        id: id,
      })
        .done(function (response) {
          showMessage(response.message || "Contact deleted successfully.");
          window.location.reload();
        })
        .fail(function (xhr) {
          const message =
            xhr.responseJSON?.message || "Unable to delete contact.";

          showMessage(message);
          console.error("Contact delete error:", xhr);
        });
    });
  });
})(document, Granite.$);
