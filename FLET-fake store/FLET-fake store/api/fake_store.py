import requests

BASE_URL = "https://fakestoreapi.com"

class FakeStoreAPI:
    @staticmethod
    def verificar_conexion() -> bool:
        """Comprueba si hay conexión enviando una pequeña consulta."""
        try:
            response = requests.get(f"{BASE_URL}/products?limit=1", timeout=3)
            return response.status_code == 200
        except Exception:
            return False

    @staticmethod
    def login(username: str, password: str) -> dict | None:
        """
        Intenta autenticar contra la API. Si la API falla por red/servidor,
        valida contra los usuarios oficiales de la API localmente para no detener la app.
        """
        headers = {
            "Content-Type": "application/json",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
        }
        
        # 1. Intentar con la API real
        try:
            res = requests.post(
                f"{BASE_URL}/auth/login",
                json={"username": username, "password": password},
                headers=headers,
                timeout=5
            )
            
            if res.status_code == 200:
                token = res.json().get("token")
                user_info = FakeStoreAPI._obtener_info_usuario(username)
                return {"token": token, "user": user_info}
            else:
                print(f"[API WARN] La API respondió con código: {res.status_code}")
        except Exception as err:
            print(f"[API ERROR] Error de conexión: {err}")

        # 2. Respaldo directo si el servidor de FakeStoreAPI no responde correctamente al POST
        usuarios_api_oficiales = {
            "johnd": {"pass": "m38rmF$", "id": 1},
            "mor_2314": {"pass": "83r5^_", "id": 2},
            "kevinryan": {"pass": "kevino300", "id": 3},
            "donat": {"pass": "ewed34", "id": 4},
            "derek": {"pass": "jlo222", "id": 5}
        }

        if username in usuarios_api_oficiales and usuarios_api_oficiales[username]["pass"] == password:
            user_id = usuarios_api_oficiales[username]["id"]
            user_info = FakeStoreAPI._obtener_info_usuario(username) or {"id": user_id, "username": username}
            return {
                "token": "fake_token_fakestore_api_fallback",
                "user": user_info
            }
            
        return None

    @staticmethod
    def _obtener_info_usuario(username: str) -> dict:
        """Obtiene la información del usuario mediante GET."""
        try:
            res_users = requests.get(f"{BASE_URL}/users", timeout=5)
            if res_users.status_code == 200:
                usuarios = res_users.json()
                return next((u for u in usuarios if u.get("username") == username), {})
        except Exception:
            pass
        return {}