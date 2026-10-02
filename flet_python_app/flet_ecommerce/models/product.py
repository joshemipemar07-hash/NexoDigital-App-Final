class Product:

    def __init__(
        self,
        id: int,
        title: str,
        price: float,
        description: str,
        category: str,
        image: str
    ):
        self.id = id
        self.title = title
        self.price = price
        self.description = description
        self.category = category
        self.image = image

    # Método de mapeo del JSON recibido de la Fake Store API hacia la clase local
    # Garantiza que los campos requeridos (imagen, título, precio, descripción, categoría) estén formateados correctamente
    @classmethod
    def from_json(cls, data: dict):

        return cls(
            id=data.get("id", 0),
            title=data.get("title", ""),
            price=float(data.get("price", 0.0)),
            description=data.get("description", ""),
            category=data.get("category", ""),
            image=data.get("image", "")
        )