import flet as ft

class HomeView(ft.View):
    def __init__(self, session: dict, on_logout):
        super().__init__(route="/home")
        self.session = session
        self.on_logout = on_logout

        self.vertical_alignment = ft.MainAxisAlignment.CENTER
        self.horizontal_alignment = ft.CrossAxisAlignment.CENTER

        user_details = session.get("user_details") or {}
        nombre_completo = ""
        if "name" in user_details and isinstance(user_details["name"], dict):
            first = user_details['name'].get('firstname', '').capitalize()
            last = user_details['name'].get('lastname', '').capitalize()
            nombre_completo = f"{first} {last}"

        self.appbar = ft.AppBar(
            title=ft.Text("Panel Principal"),
            actions=[ft.IconButton(ft.Icons.LOGOUT, on_click=lambda e: self.on_logout())]
        )

        self.controls = [
            ft.Text(f"¡Bienvenido, {nombre_completo if nombre_completo else 'Usuario'}!", size=22, weight=ft.FontWeight.BOLD),
            ft.Text(f"ID de Usuario (API): #{session.get('user_id')}", size=16),
            ft.Text(f"Rol asignado: {session.get('role')}", size=16, weight=ft.FontWeight.BOLD, color=ft.Colors.BLUE),
            ft.Container(height=20),
            ft.ElevatedButton("Cerrar Sesión", on_click=lambda e: self.on_logout())
        ]