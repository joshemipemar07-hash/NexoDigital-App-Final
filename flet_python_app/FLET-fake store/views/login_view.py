import flet as ft
from api.fake_store import FakeStoreAPI

class LoginView(ft.View):
    def __init__(self, on_login_success):
        super().__init__(route="/login")
        self.on_login_success = on_login_success

        self.vertical_alignment = ft.MainAxisAlignment.CENTER
        self.horizontal_alignment = ft.CrossAxisAlignment.CENTER

        self.txt_user = ft.TextField(label="Usuario", width=300, value="derekr")
        self.txt_pass = ft.TextField(label="Contraseña", password=True, can_reveal_password=True, width=300, value="123456")
        self.lbl_error = ft.Text(color=ft.Colors.RED)
        self.btn_login = ft.ElevatedButton("Iniciar Sesión", on_click=self.login_click)

        self.controls = [
            ft.Text("Iniciar Sesión", size=24, weight=ft.FontWeight.BOLD),
            self.txt_user,
            self.txt_pass,
            self.btn_login,
            self.lbl_error
        ]

    async def login_click(self, e):
        self.lbl_error.value = ""
        self.page.update()

        usr = self.txt_user.value.strip()
        pwd = self.txt_pass.value.strip()

        if not usr or not pwd:
            self.lbl_error.value = "Por favor ingrese usuario y contraseña."
            self.page.update()
            return

        if not await FakeStoreAPI.verificar_conexion():
            self.lbl_error.value = "Sin conexión a internet o API no disponible."
            self.page.update()
            return

        # Intento de autenticación con la API
        resultado = await FakeStoreAPI.login(usr, pwd)

        # Mapeo local de ID/Rol según las reglas de negocio
        if resultado and resultado.get("token"):
            user_data = resultado.get("user", {})
            user_id = user_data.get("id", 1)
            token = resultado["token"]
        else:
            # MODO PRUEBA LOCAL: Si la API rechaza las credenciales, 
            # permitimos el acceso local asignando un ID según el usuario para probar los roles.
            token = "fake_token_local_123"
            if usr in ["johnd", "admin"]:
                user_id = 1
                user_data = {"name": {"firstname": "John", "lastname": "Doe"}}
            elif usr == "auditor":
                user_id = 3
                user_data = {"name": {"firstname": "Auditor", "lastname": "General"}}
            else:
                user_id = 10  # Asigna ID de Cliente a cualquier otro usuario
                user_data = {"name": {"firstname": usr.capitalize(), "lastname": "Cliente"}}

        # Determinación de Rol
        if user_id in [1, 2]:
            rol = "Administrador"
        elif user_id == 3:
            rol = "Auditor"
        else:
            rol = "Cliente"

        self.txt_user.value = ""
        self.txt_pass.value = ""
        
        self.on_login_success({
            "token": token,
            "role": rol,
            "user_id": user_id,
            "user_details": user_data
        })