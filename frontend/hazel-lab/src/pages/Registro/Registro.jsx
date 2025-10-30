// src/pages/Registro/Registro.jsx
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { crearUsuario } from "../../services/api";
import "../InicioSesion/Login-y-registro.css";

export function Registro() {
  const navigate = useNavigate();

  // Estado para manejar los datos del formulario de registro
  const [formData, setFormData] = useState({
    run: "",
    nombre: "",
    apellidos: "",
    correo: "",
    fechaNacimiento: "",
    region: "",
    comuna: "",
    direccion: "",
    clave: "",
    confirmarClave: "",
  });

  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  // Manejar cambios en los campos del formulario
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Validaciones de formato para RUN y correo electrónico
  const validarRun = (run) => /^[0-9]{7,8}[0-9Kk]{1}$/.test(run);
  const validarCorreo = (correo) =>
    /^[a-zA-Z0-9._%+-]+@(duoc\.cl|profesor\.duoc\.cl|gmail\.com)$/.test(correo);

  // Procesar el envío del formulario de registro
  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    const {
      run,
      nombre,
      apellidos,
      correo,
      fechaNacimiento,
      region,
      comuna,
      direccion,
      clave,
      confirmarClave,
    } = formData;

    // Validaciones frontend antes del envío
    if (!validarRun(run)) {
      setErrorMsg("RUN inválido. Ej: 19011022K");
      return;
    }
    if (!validarCorreo(correo)) {
      setErrorMsg("Correo inválido. Solo @duoc.cl / @profesor.duoc.cl / @gmail.com");
      return;
    }
    if (clave.length < 4 || clave.length > 50) {
      setErrorMsg("La contraseña debe tener al menos 4 caracteres (se recomienda más).");
      return;
    }
    if (clave !== confirmarClave) {
      setErrorMsg("Las contraseñas no coinciden.");
      return;
    }

    // Preparar datos para enviar al backend según estructura esperada
    const payload = {
      username: `${nombre} ${apellidos}`.trim() || nombre || apellidos || run,
      email: correo.trim().toLowerCase(),
      password: clave,
      role: "Cliente",
      status: "activo",
    };

    try {
      setSubmitting(true);
      const res = await crearUsuario(payload);
      
      // Guardar usuario en localStorage y redirigir
      localStorage.setItem("usuarioLogueado", JSON.stringify(res.data));
      alert("Usuario creado correctamente. Iniciando sesión...");
      navigate("/");
    } catch (err) {
      console.error("Error al crear usuario:", err);

      // Manejar mensajes de error del servidor
      const serverMsg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        (typeof err?.response?.data === "string" ? err.response.data : null);

      if (serverMsg) {
        setErrorMsg(String(serverMsg));
      } else {
        setErrorMsg("No se pudo registrar el usuario. Verifica los datos e inténtalo nuevamente.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="container my-5">
      <div className="login-container">
        <h1 className="login-title">Registro de Usuario</h1>

        <form onSubmit={handleSubmit} className="login-form">
          {/* Mostrar mensajes de error */}
          {errorMsg && (
            <div style={{ color: "#8B3E2F", marginBottom: "12px", textAlign: "center" }}>
              {errorMsg}
            </div>
          )}

          {/* Campo RUN */}
          <label htmlFor="run">RUN</label>
          <input
            type="text"
            id="run"
            name="run"
            required
            minLength="7"
            maxLength="9"
            placeholder="Ej: 19011022K"
            value={formData.run}
            onChange={handleChange}
          />

          {/* Campo Nombre */}
          <label htmlFor="nombre">Nombre</label>
          <input
            type="text"
            id="nombre"
            name="nombre"
            required
            maxLength="50"
            placeholder="Ingrese su nombre"
            value={formData.nombre}
            onChange={handleChange}
          />

          {/* Campo Apellidos */}
          <label htmlFor="apellidos">Apellidos</label>
          <input
            type="text"
            id="apellidos"
            name="apellidos"
            required
            maxLength="100"
            placeholder="Ingrese sus apellidos"
            value={formData.apellidos}
            onChange={handleChange}
          />

          {/* Campo Correo Electrónico */}
          <label htmlFor="correo">Correo electrónico</label>
          <input
            type="email"
            id="correo"
            name="correo"
            required
            maxLength="100"
            placeholder="usuario@dominio.com"
            value={formData.correo}
            onChange={handleChange}
          />

          {/* Campo Fecha de Nacimiento (Opcional) */}
          <label htmlFor="fechaNacimiento">Fecha de Nacimiento (opcional)</label>
          <input
            type="date"
            id="fechaNacimiento"
            name="fechaNacimiento"
            value={formData.fechaNacimiento}
            onChange={handleChange}
          />

          {/* Campo Región */}
          <label htmlFor="region">Región</label>
          <input
            type="text"
            id="region"
            name="region"
            required
            placeholder="Ej: Valparaíso"
            value={formData.region}
            onChange={handleChange}
          />

          {/* Campo Comuna */}
          <label htmlFor="comuna">Comuna</label>
          <input
            type="text"
            id="comuna"
            name="comuna"
            required
            placeholder="Ej: Viña del Mar"
            value={formData.comuna}
            onChange={handleChange}
          />

          {/* Campo Dirección */}
          <label htmlFor="direccion">Dirección</label>
          <textarea
            id="direccion"
            name="direccion"
            required
            maxLength="300"
            placeholder="Ingrese su dirección"
            value={formData.direccion}
            onChange={handleChange}
          ></textarea>

          {/* Campo Contraseña */}
          <label htmlFor="clave">Contraseña</label>
          <input
            type="password"
            id="clave"
            name="clave"
            required
            minLength="4"
            maxLength="50"
            placeholder="Ingrese una contraseña"
            value={formData.clave}
            onChange={handleChange}
          />

          {/* Campo Confirmar Contraseña */}
          <label htmlFor="confirmarClave">Confirmar Contraseña</label>
          <input
            type="password"
            id="confirmarClave"
            name="confirmarClave"
            required
            minLength="4"
            maxLength="50"
            placeholder="Repita su contraseña"
            value={formData.confirmarClave}
            onChange={handleChange}
          />

          {/* Botón de envío del formulario */}
          <button type="submit" className="login-btn mt-3" disabled={submitting}>
            {submitting ? "Creando cuenta..." : "Crear Cuenta"}
          </button>

          {/* Enlaces de navegación */}
          <div className="text-center mt-3">
            <Link to="/" className="login-link me-3">
              Volver al Home
            </Link>
            <Link to="/login" className="login-link">
              Ir a Iniciar Sesión
            </Link>
          </div>
        </form>
      </div>
    </main>
  );
}