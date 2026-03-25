const express = require('express');
const bcrypt = require('bcryptjs');
const app = express();
app.use(express.json());
const cors = require('cors');
app.use(cors()); 

const users = []; // Base de datos temporal en memoria
//para el crud
let notas = [
    { id: 1, titulo: "Nota de prueba", contenido: "Esta es una nota desde Docker" }
];
// Verificar API 
app.get('/', (req, res) => {
    res.send("API de Node.js activa y funcionando desde Docker");
});

// Registro con Bcrypt (metodo de seguridad para proteger contraseñas)
app.post('/register', async (req, res) => {
    const { username, password } = req.body;
    if (users.find(u => u.username === username)) {
        return res.status(400).json({ message: "Usuario ya existe" });
    }
    const hashedPassword = await bcrypt.hash(password, 10);
    users.push({ username, password: hashedPassword });
    res.status(201).json({ message: "Registro exitoso" });
});

//Login 
app.post('/login', async (req, res) => {
    
    const { username, password } = req.body;
    if (!username || !password) {
        return res.status(400).json({ message: "Faltan datos en el JSON" });
    }


    const user = users.find(u => u.username === username);
    if (user && await bcrypt.compare(password, user.password)) {
        res.json({ message: `Bienvenido ${username}` });
    } else {
        res.status(401).json({ message: "Credenciales incorrectas" });
    }
});

// Escuchar en el puerto 5000 
app.listen(5000, '0.0.0.0', () => {
    console.log('--- SERVIDOR ACTIVO PARA LA RED ---');
    console.log('Escuchando en el puerto 5000');
});

//operaciones del CRUD
// Leer todas (GET)
app.get('/notas', (req, res) => {
    console.log("Enviando lista de notas...");
    res.json(notas);
});

// Crear una (POST)
app.post('/notas', (req, res) => {
    const { titulo, contenido } = req.body;
    const nuevaNota = { id: Date.now(), titulo, contenido };
    notas.push(nuevaNota);
    console.log("Nota creada:", nuevaNota);
    res.status(201).json(nuevaNota);
});

// Borrar una (DELETE)
app.delete('/notas/:id', (req, res) => {
    const { id } = req.params;
    const inicial = notas.length;
    notas = notas.filter(n => n.id !== parseInt(id));
    
    if (notas.length < inicial) {
        // Respondemos con JSON para que Android no de error
        res.json({ status: "deleted", message: "Nota eliminada correctamente" });
    } else {
        res.status(404).json({ error: "Nota no encontrada" });
    }
});

// Actualizar (PUT) - Opcional para el 10
app.put('/notas/:id', (req, res) => {
    const { id } = req.params;
    const { titulo, contenido } = req.body;
    const index = notas.findIndex(n => n.id === parseInt(id));
    if (index !== -1) {
        notas[index] = { id: parseInt(id), titulo, contenido };
        res.json(notas[index]);
    } else {
        res.status(404).send("No encontrada");
    }
});