import flet as ft
from views.login_view import LoginView
from views.home_view import HomeView
from views.product_detail_view import ProductDetailView

async def main(page: ft.Page):
    page.title = "App con Autenticación y Catálogo Flet"
    
    session = {"token": None, "role": None, "user_id": None, "user_details": None}

    def on_login_success(session_data):
        session.update(session_data)
        page.go("/home")

    def on_logout():
        session.update({"token": None, "role": None, "user_id": None, "user_details": None})
        page.go("/login")

    def on_select_product(product_id: int):
        page.go(f"/product/{product_id}")

    def route_change(route):
        page.views.clear()
        
        if session["token"] is None:
            page.views.append(LoginView(on_login_success))
            page.update()
            return

        if page.route == "/home":
            home_view = HomeView(session, on_logout, on_select_product)
            page.views.append(home_view)
            page.update()
            page.run_task(home_view.cargar_catalogo)

        elif page.route.startswith("/product/"):
            try:
                product_id = int(page.route.split("/")[-1])
                detail_view = ProductDetailView(product_id, session, on_back=lambda: page.go("/home"))
                page.views.append(detail_view)
                page.update()
                page.run_task(detail_view.cargar_detalle)
            except ValueError:
                page.go("/home")
        else:
            page.go("/home")

    page.on_route_change = route_change
    page.go("/login")

if __name__ == "__main__":
    ft.app(target=main)