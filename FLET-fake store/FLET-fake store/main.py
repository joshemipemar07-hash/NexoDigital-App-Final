import flet as ft
from views.login_view import LoginView
from views.home_view import HomeView

def main(page: ft.Page):
    page.title = "App con Autenticación Flet"
    
    session = {"token": None, "role": None, "user_id": None, "user_details": None}

    def on_login_success(session_data):
        session.update(session_data)
        page.go("/home")

    def on_logout():
        session.update({"token": None, "role": None, "user_id": None, "user_details": None})
        page.go("/login")

    def route_change(route):
        page.views.clear()
        if page.route == "/home" and session["token"] is not None:
            page.views.append(HomeView(session, on_logout))
        else:
            page.views.append(LoginView(on_login_success))
        page.update()

    page.on_route_change = route_change
    page.go("/login")

if __name__ == "__main__":
    ft.app(target=main)