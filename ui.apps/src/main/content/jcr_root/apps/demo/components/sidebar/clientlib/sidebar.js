document.addEventListener("DOMContentLoaded", function () {
  const sidebarComponents = document.querySelectorAll(".zav-sidebar-component");

  sidebarComponents.forEach(function (component) {
    const menuButton = component.querySelector(".zav-menu-toggle");
    const closeButton = component.querySelector(".zav-sidebar-close");
    const overlay = component.querySelector("[data-sidebar-overlay]");
    const sidebar = component.querySelector(".zav-sidebar");

    if (!menuButton || !closeButton || !overlay || !sidebar) {
      return;
    }

    function openSidebar() {
      component.classList.add("is-open");
      menuButton.setAttribute("aria-expanded", "true");
      sidebar.setAttribute("aria-hidden", "false");
    }

    function closeSidebar() {
      component.classList.remove("is-open");
      menuButton.setAttribute("aria-expanded", "false");
      sidebar.setAttribute("aria-hidden", "true");
    }

    menuButton.addEventListener("click", openSidebar);
    closeButton.addEventListener("click", closeSidebar);
    overlay.addEventListener("click", closeSidebar);

    document.addEventListener("keydown", function (event) {
      if (event.key === "Escape") {
        closeSidebar();
      }
    });
  });
});
