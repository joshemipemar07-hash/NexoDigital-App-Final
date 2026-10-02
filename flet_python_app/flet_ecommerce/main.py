import flet as ft

from views.login_view import LoginView
from views.home_view import HomeView
from views.product_detail_view import ProductDetailView
from views.product_form_view import ProductFormView


async def main(page: ft.Page):

    page.title = "App con Autenticación y Catálogo Flet"

    session = {
        "token": None,
        "role": None,
        "user_id": None,
        "user_details": None
    }

    def on_login_success(session_data):
        session.update(session_data)
        page.go("/home")

    def on_logout():
        session.update({
            "token": None,
            "role": None,
            "user_id": None,
            "user_details": None
        })
        page.go("/login")

    def on_select_product(product_id: int):
        page.go(f"/product/{product_id}")

    def ir_a_crear():
        page.go("/product/new")

    def ir_a_editar(product_id: int):
        page.go(f"/product/edit/{product_id}")

    def route_change(route):

        page.views.clear()

        # Protección de rutas
        if session["token"] is None:
            page.views.append(
                LoginView(on_login_success)
            )
            page.update()
            return

        # =========================
        # CATÁLOGO
        # =========================
        if page.route == "/home":

            home_view = HomeView(
                session,
                on_logout,
                on_select_product,
                ir_a_crear
            )

            page.views.append(home_view)
            page.update()

            page.run_task(
                home_view.cargar_catalogo
            )

        # =========================
        # CREAR PRODUCTO
        # =========================
        elif page.route == "/product/new":

            if session.get("role") != "Administrador":
                page.go("/home")
                return

            form_view = ProductFormView(
                session=session,
                mode="create",
                product_id=None,
                on_success=lambda product: page.go("/home"),
                on_cancel=lambda: page.go("/home")
            )

            page.views.append(form_view)
            page.update()

        # =========================
        # EDITAR PRODUCTO
        # =========================
        elif page.route.startswith("/product/edit/"):

            if session.get("role") != "Administrador":
                page.go("/home")
                return

            try:
                product_id = int(
                    page.route.split("/")[-1]
                )

                form_view = ProductFormView(
                    session=session,
                    mode="edit",
                    product_id=product_id,
                    on_success=lambda product: page.go(
                        f"/product/{product_id}"
                    ),
                    on_cancel=lambda: page.go(
                        f"/product/{product_id}"
                    )
                )

                page.views.append(form_view)
                page.update()

                page.run_task(
                    form_view.cargar_producto
                )

            except ValueError:
                page.go("/home")

        # =========================
        # DETALLE DEL PRODUCTO
        # =========================
        elif page.route.startswith("/product/"):

            try:

                product_id = int(
                    page.route.split("/")[-1]
                )

                detail_view = ProductDetailView(
                    product_id=product_id,
                    session=session,
                    on_back=lambda: page.go("/home"),
                    on_edit=ir_a_editar
                )

                page.views.append(detail_view)
                page.update()

                page.run_task(
                    detail_view.cargar_detalle
                )

            except ValueError:
                page.go("/home")

        else:
            page.go("/home")

    page.on_route_change = route_change

    page.go("/login")


if __name__ == "__main__":
    ft.app(target=main)