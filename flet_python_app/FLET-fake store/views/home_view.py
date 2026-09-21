import flet as ft
from api.fake_store import FakeStoreAPI

class HomeView(ft.View):
    def __init__(self, session: dict, on_logout, on_select_product):
        super().__init__(route="/home")
        self.session = session
        self.on_logout = on_logout
        self.on_select_product = on_select_product

        user_details = session.get("user_details") or {}
        nombre_completo = ""
        if "name" in user_details and isinstance(user_details["name"], dict):
            first = user_details['name'].get('firstname', '').capitalize()
            last = user_details['name'].get('lastname', '').capitalize()
            nombre_completo = f"{first} {last}"

        self.appbar = ft.AppBar(
            title=ft.Text(f"Bienvenido, {nombre_completo if nombre_completo else 'Usuario'} ({session.get('role')})"),
            actions=[ft.IconButton(ft.Icons.LOGOUT, on_click=lambda e: self.on_logout())]
        )

        self.dd_categories = ft.Dropdown(
            label="Categoría",
            value="TODAS",
            width=220,
            options=[ft.dropdown.Option(key="TODAS", text="Todas las categorías")]
        )

        self.btn_filtrar = ft.ElevatedButton(
            "Filtrar",
            icon=ft.Icons.FILTER_ALT,
            on_click=lambda e: self.page.run_task(self.cargar_catalogo)
        )

        self.filter_container = ft.Container(
            padding=ft.Padding(10, 10, 10, 5),
            content=ft.Row(
                controls=[self.dd_categories, self.btn_filtrar],
                alignment=ft.MainAxisAlignment.START,
                spacing=10
            )
        )

        self.content_area = ft.Container(expand=True)
        self.controls = [
            self.filter_container,
            self.content_area
        ]

    async def cargar_categorias(self):
        try:
            categorias = await FakeStoreAPI.obtener_categorias()
            options = [ft.dropdown.Option(key="TODAS", text="Todas las categorías")]
            
            for cat in categorias:
                options.append(ft.dropdown.Option(key=str(cat), text=str(cat).capitalize()))
                
            self.dd_categories.options = options
            if self.page:
                self.page.update()
        except Exception as err:
            print(f"[CATEGORIES ERROR]: {err}")

    async def cargar_catalogo(self, e=None):
        if len(self.dd_categories.options) <= 1:
            await self.cargar_categorias()

        categoria = self.dd_categories.value

        self.content_area.content = ft.Column(
            controls=[
                ft.ProgressRing(),
                ft.Text("Cargando productos...", size=16)
            ],
            alignment=ft.MainAxisAlignment.CENTER,
            horizontal_alignment=ft.CrossAxisAlignment.CENTER,
            expand=True
        )
        if self.page:
            self.page.update()

        try:
            if categoria and categoria != "TODAS":
                productos = await FakeStoreAPI.obtener_productos_por_categoria(categoria)
            else:
                productos = await FakeStoreAPI.obtener_productos()

            items = []
            for p in productos:
                # Se utiliza ft.Container con on_click directo
                card_content = ft.Container(
                    padding=10,
                    on_click=lambda e, pid=p.id: self.on_select_product(pid),
                    content=ft.Row(
                        controls=[
                            ft.Image(src=p.image, width=80, height=80, fit="contain"),
                            ft.Column(
                                controls=[
                                    ft.Text(p.title, weight=ft.FontWeight.BOLD, size=14, max_lines=2, overflow=ft.TextOverflow.ELLIPSIS),
                                    ft.Text(f"${p.price:.2f}", color=ft.Colors.GREEN_700, weight=ft.FontWeight.BOLD, size=16)
                                ],
                                expand=True
                            )
                        ],
                        vertical_alignment=ft.CrossAxisAlignment.CENTER
                    )
                )
                items.append(ft.Card(content=card_content))

            self.content_area.content = ft.ListView(
                key=f"cat_list_{categoria}",
                controls=items, 
                expand=True, 
                spacing=10, 
                padding=10
            )

        except Exception as err:
            print(f"[CATALOG ERROR]: {err}")
            self.content_area.content = ft.Column(
                controls=[
                    ft.Icon(ft.Icons.SIGNAL_WIFI_OFF, color=ft.Colors.RED_400, size=50),
                    ft.Text("Error de red al consultar el catálogo.", color=ft.Colors.RED_400, size=16),
                    ft.Text(f"Detalle: {str(err)}", color=ft.Colors.GREY_600, size=12),
                    ft.ElevatedButton(
                        "Reintentar", 
                        icon=ft.Icons.REFRESH, 
                        on_click=lambda e: self.page.run_task(self.cargar_catalogo)
                    )
                ],
                alignment=ft.MainAxisAlignment.CENTER,
                horizontal_alignment=ft.CrossAxisAlignment.CENTER,
                expand=True
            )
            
        if self.page:
            self.page.update()