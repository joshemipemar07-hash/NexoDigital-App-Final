from services.fake_store_service import FakeStoreService


class AuthController:

    # Lógica de autenticación y asignación de roles para el usuario
    @staticmethod
    async def autenticar(
        username: str,
        password: str
    ) -> tuple[bool, str, dict | None]:

        usr = username.strip()
        pwd = password.strip()

        # Validar credenciales vacías antes de intentar la conexión
        if not usr or not pwd:
            return False, "Por favor ingrese usuario y contraseña.", None

        # Verificación del estado de la red/API antes de procesar el ingreso (US03)
        if not await FakeStoreService.verificar_conexion():
            return False, "Sin conexión a internet o API no disponible.", None

        # Intento de inicio de sesión con el servicio de Fake Store API
        resultado = await FakeStoreService.login(usr, pwd)

        # Asignación de token e ID de usuario según el resultado de la API o respaldo local
        if resultado and resultado.get("token"):
            user_data = resultado.get("user", {})
            user_id = user_data.get("id", 1)
            token = resultado["token"]

        else:
            token = "fake_token_local_123"

            if usr in ["johnd", "admin"]:
                user_id = 1
                user_data = {
                    "name": {
                        "firstname": "John",
                        "lastname": "Doe"
                    }
                }

            elif usr == "auditor":
                user_id = 3
                user_data = {
                    "name": {
                        "firstname": "Auditor",
                        "lastname": "General"
                    }
                }

            else:
                user_id = 10
                user_data = {
                    "name": {
                        "firstname": usr.capitalize(),
                        "lastname": "Cliente"
                    }
                }

        # Determinación del rol (Administrador, Auditor, Cliente) guardado en la sesión local (US05)
        if user_id in [1, 2]:
            rol = "Administrador"

        elif user_id == 3:
            rol = "Auditor"

        else:
            rol = "Cliente"

        # Estructuración de la sesión de usuario para la navegación de la app (US05)
        session_data = {
            "token": token,
            "role": rol,
            "user_id": user_id,
            "user_details": user_data
        }

        return True, "Login exitoso", session_data