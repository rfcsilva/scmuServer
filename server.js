const express = require('express');
const bodyParser = require('body-parser');
const app = express();

// Listen to the App Engine-specified port, or 8080 otherwise
const PORT = process.env.PORT || 8080;

/*
app.get('/', (req, res) => {
  res.send('Hello from App Engine!');
});
*/

app.use(express.static('public'))
app.use(bodyParser.json())
app.use(require('./controllers'))


app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}...`);
});
