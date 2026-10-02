import flet as ft

from controllers.auth_controller import AuthController


# Vista para la autenticación de usuarios e inicio de sesión
class LoginView(ft.View):

    def __init__(self, on_login_success):

        super().__init__(
            route="/login"
        )

        self.on_login_success = on_login_success

        # Configuración de alineación visual del formulario de login
        self.vertical_alignment = ft.MainAxisAlignment.CENTER
        self.horizontal_alignment = ft.CrossAxisAlignment.CENTER

        # Campos de entrada de datos, mensajes de error y botón de acción
        self.txt_user = ft.TextField(
            label="Usuario",
            width=300,
            value="derekr"
        )

        self.txt_pass = ft.TextField(
            label="Contraseña",
            password=True,
            can_reveal_password=True,
            width=300,
            value="123456"
        )

        self.lbl_error = ft.Text(
            color=ft.Colors.RED
        )

        self.btn_login = ft.ElevatedButton(
            "Iniciar Sesión",
            on_click=self.login_click
        )

        # Construcción de la jerarquía visual de la pantalla de login
        self.controls = [
            ft.Text(
                "Iniciar Sesión",
                size=24,
                weight=ft.FontWeight.BOLD
            ),

            self.txt_user,

            self.txt_pass,

            self.btn_login,

            self.lbl_error
        ]

    # Procesa el intento de login, valida la conectividad y genera los datos de sesión/rol (US03 / US05)
    async def login_click(self, e):

        self.lbl_error.value = ""

        self.page.update()

        # Validación de credenciales y red mediante el controlador de autenticación (US03)
        exito, mensaje, session_data = await AuthController.autenticar(
            self.txt_user.value,
            self.txt_pass.value
        )

        # Manejo de error en pantalla si falla la autenticación o la conexión a la API (US03)
        if not exito:

            self.lbl_error.value = mensaje

            self.page.update()

            return

        # Limpieza de formulario y redirección tras un inicio de sesión exitoso
        self.txt_user.value = ""
        self.txt_pass.value = ""

        self.on_login_success(session_data)