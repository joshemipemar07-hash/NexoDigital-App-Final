import flet as ft
from api.fake_store import FakeStoreAPI

class ProductDetailView(ft.View):
    def __init__(self, product_id: int, session: dict, on_back):
        super().__init__(route=f"/product/{product_id}")
        self.product_id = product_id
        self.session = session
        self.on_back = on_back

        self.appbar = ft.AppBar(
            title=ft.Text("Detalle del Producto"),
            leading=ft.IconButton(ft.Icons.ARROW_BACK, on_click=lambda e: self.on_back())
        )

        self.content_area = ft.Container(
            expand=True,
            padding=20,
            content=ft.Column(
                controls=[
                    ft.ProgressRing(),
                    ft.Text("Cargando detalle...", size=16)
                ],
                alignment=ft.MainAxisAlignment.CENTER,
                horizontal_alignment=ft.CrossAxisAlignment.CENTER,
                expand=True
            )
        )

        self.controls = [self.content_area]

    async def cargar_detalle(self):
        try:
            product = await FakeStoreAPI.obtener_producto_por_id(self.product_id)
            if not product:
                await self._mostrar_error_y_regresar("Producto no disponible")
                return

            self._construir_ui(product)
        except Exception as err:
            print(f"[PRODUCT DETAIL ERROR]: {err}")
            await self._mostrar_error_y_regresar("Error al cargar el producto")

    async def _mostrar_error_y_regresar(self, mensaje: str):
        if self.page:
            dlg = ft.AlertDialog(
                title=ft.Text("Alerta"),
                content=ft.Text(mensaje),
                actions=[
                    ft.TextButton("Aceptar", on_click=lambda e: self._cerrar_dialogo_y_volver(dlg))
                ],
                actions_alignment=ft.MainAxisAlignment.END,
            )
            self.page.dialog = dlg
            dlg.open = True
            self.page.update()

    def _cerrar_dialogo_y_volver(self, dlg):
        dlg.open = False
        if self.page:
            self.page.update()
        self.on_back()

    def _construir_ui(self, product):
        # Leemos el rol estrictamente de la sesión local
        role = self.session.get("role")

        elementos_detalle = [
            ft.Image(src=product.image, height=200, fit="contain"),
            ft.Text(product.title, size=20, weight=ft.FontWeight.BOLD),
            ft.Text(f"Categoría: {product.category.capitalize()}", size=14, color=ft.Colors.GREY_700),
            ft.Text(f"${product.price:.2f}", size=22, weight=ft.FontWeight.BOLD, color=ft.Colors.GREEN_700),
            ft.Divider(),
            ft.Text("Descripción:", size=16, weight=ft.FontWeight.BOLD),
            ft.Text(product.description, size=14, color=ft.Colors.GREY_800),
        ]

        # REGLA DE NEGOCIO: Los botones SOLO se instancian si el rol es Administrador
        if role == "Administrador":
            elementos_detalle.append(ft.Divider())
            elementos_detalle.append(
                ft.Row(
                    controls=[
                        ft.ElevatedButton(
                            "Editar",
                            icon=ft.Icons.EDIT,
                            style=ft.ButtonStyle(color=ft.Colors.WHITE, bgcolor=ft.Colors.BLUE_600)
                        ),
                        ft.ElevatedButton(
                            "Eliminar",
                            icon=ft.Icons.DELETE,
                            style=ft.ButtonStyle(color=ft.Colors.WHITE, bgcolor=ft.Colors.RED_600)
                        ),
                    ],
                    alignment=ft.MainAxisAlignment.CENTER,
                    spacing=15
                )
            )

        self.content_area.content = ft.Column(
            controls=elementos_detalle,
            spacing=12,
            scroll=ft.ScrollMode.AUTO,
            expand=True
        )

        if self.page:
            self.page.update()