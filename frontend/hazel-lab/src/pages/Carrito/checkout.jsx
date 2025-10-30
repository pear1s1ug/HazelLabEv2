import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getItemsCarritoPorUsuario } from "../../services/api";

export default function Checkout() {
  const navigate = useNavigate();

  // Estados del componente
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Estados para opciones de envío y pago
  const [metodoEnvio, setMetodoEnvio] = useState("retiro");
  const [metodoPago, setMetodoPago] = useState("");

  // Formateador de precios para moneda chilena
  const fmt = new Intl.NumberFormat("es-CL", { 
    style: "currency", 
    currency: "CLP" 
  });

  // Obtener información del usuario desde localStorage
  const usuario = JSON.parse(localStorage.getItem("usuarioLogueado"));

  // Cargar items del carrito desde el backend
  useEffect(() => {
    const cargarCarrito = async () => {
      if (!usuario) {
        setError("Debes iniciar sesión para continuar con el pago.");
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        const respuesta = await getItemsCarritoPorUsuario(usuario.id);
        const datosCarrito = Array.isArray(respuesta?.data) ? respuesta.data : [];
        setItems(datosCarrito);
      } catch (error) {
        console.error("Error al cargar el carrito:", error);
        setError("No se pudo cargar tu carrito.");
      } finally {
        setLoading(false);
      }
    };
    cargarCarrito();
  }, []);

  // Calcular subtotal basado en los items del carrito
  const subtotal = useMemo(
    () => items.reduce((acumulador, item) => 
      acumulador + Number(item.producto?.cost ?? 0) * Number(item.quantity ?? 1), 0),
    [items]
  );

  // Calcular costo de envío según el método seleccionado
  const costoEnvio = useMemo(() => {
    if (metodoEnvio === "estandar") return 3990;
    if (metodoEnvio === "express") return 6990;
    return 0; // Retiro en tienda es gratuito
  }, [metodoEnvio]);

  // Calcular total final incluyendo envío
  const total = useMemo(() => subtotal + costoEnvio, [subtotal, costoEnvio]);

  // Manejar la confirmación de la compra
  const confirmarCompra = () => {
    if (!usuario) {
      navigate("/login");
      return;
    }
    if (!items.length) {
      alert("Tu carrito está vacío.");
      navigate("/carrito");
      return;
    }
    if (!metodoPago) {
      alert("Selecciona un método de pago.");
      return;
    }

    // Simulación de confirmación de pedido
    alert("Proceso de compra completado exitosamente.\n\n" +
      `Método de envío: ${metodoEnvio}\n` +
      `Método de pago: ${metodoPago}\n` +
      `Total a pagar: ${fmt.format(total)}`
    );
    
    // Aquí se integraría la llamada al backend para crear la orden
  };

  // Estados de carga
  if (loading) return <p className="text-center my-5">Preparando tu pedido...</p>;

  // Estados de error
  if (error) {
    return (
      <div className="container my-5 text-center">
        <p>{error}</p>
        {!usuario && (
          <Link to="/login" className="btn btn-primary mt-3">
            Iniciar sesión
          </Link>
        )}
      </div>
    );
  }

  return (
    <main className="container my-5" style={{ maxWidth: 1100 }}>
      <h1 className="mb-4" style={{ color: "#587042", fontWeight: 700, textAlign: "center" }}>
        Finalizar Compra
      </h1>

      {items.length === 0 ? (
        <div className="text-center">
          <p>No tienes productos en tu carrito.</p>
          <Link to="/productos" className="btn btn-primary">Explorar Productos</Link>
        </div>
      ) : (
        <div className="row g-3">
          {/* Sección de configuración de envío y pago */}
          <section className="col-12 col-lg-7">
            <div className="p-3 border rounded-3" style={{ background: "#fafafa" }}>
              <h5 className="mb-3" style={{ color: "#587042", fontWeight: 700 }}>
                Método de Entrega
              </h5>
              <select
                className="form-select mb-3"
                value={metodoEnvio}
                onChange={(e) => setMetodoEnvio(e.target.value)}
              >
                <option value="retiro">Retiro en tienda (Gratis)</option>
                <option value="estandar">Envío estándar ({fmt.format(3990)})</option>
                <option value="express">Envío express ({fmt.format(6990)})</option>
              </select>

              <h5 className="mb-3" style={{ color: "#587042", fontWeight: 700 }}>
                Método de Pago
              </h5>
              <select
                className="form-select"
                value={metodoPago}
                onChange={(e) => setMetodoPago(e.target.value)}
              >
                <option value="">Selecciona método de pago</option>
                <option value="tarjeta">Tarjeta débito/crédito</option>
                <option value="transferencia">Transferencia bancaria</option>
                <option value="efectivo">Efectivo (solo retiro)</option>
              </select>

              <div className="d-flex gap-2 mt-3">
                <Link to="/carrito" className="btn btn-light">
                  Volver al carrito
                </Link>
                <button 
                  className="btn" 
                  style={{ background: "#587042", color: "white", fontWeight: 600 }} 
                  onClick={confirmarCompra}
                >
                  Confirmar Compra
                </button>
              </div>
            </div>
          </section>

          {/* Sección de resumen del pedido */}
          <aside className="col-12 col-lg-5">
            <div className="p-3 border rounded-3">
              <h5 className="mb-3" style={{ color: "#587042", fontWeight: 700 }}>
                Resumen del Pedido
              </h5>

              <div className="table-responsive">
                <table className="table align-middle">
                  <thead>
                    <tr>
                      <th style={{ width: "55%" }}>Producto</th>
                      <th className="text-end" style={{ width: "15%" }}>Cantidad</th>
                      <th className="text-end" style={{ width: "30%" }}>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {items.map((item) => {
                      const nombre = item.producto?.name ?? "Producto";
                      const precio = Number(item.producto?.cost ?? 0);
                      const cantidad = Number(item.quantity ?? 1);
                      return (
                        <tr key={item.id}>
                          <td>{nombre}</td>
                          <td className="text-end">x{cantidad}</td>
                          <td className="text-end">{fmt.format(precio * cantidad)}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              <hr />

              <div className="d-flex justify-content-between">
                <span>Subtotal</span>
                <span>{fmt.format(subtotal)}</span>
              </div>
              <div className="d-flex justify-content-between">
                <span>Costo de Envío</span>
                <span>{metodoEnvio === "retiro" ? "Gratis" : fmt.format(costoEnvio)}</span>
              </div>

              <div className="d-flex justify-content-between mt-2" style={{ fontSize: 18, fontWeight: 700 }}>
                <span>Total Final</span>
                <span>{fmt.format(total)}</span>
              </div>
            </div>
          </aside>
        </div>
      )}
    </main>
  );
}