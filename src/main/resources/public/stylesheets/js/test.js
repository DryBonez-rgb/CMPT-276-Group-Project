const Easypost = require('@easypost/api');
const api = new Easypost('<EZAK580e28d06b9e4bc0b5183ec705ae17e5i2eKA3xwOtC2ZTcXkjDHkQ>');

const address = new api.Address({
    street1: '417 MONTGOMERY ST',
    street2: 'FLOOR 5',
    city: 'SAN FRANCISCO',
    state: 'CA',
    zip: '94104',
    country: 'US',
    company: 'EasyPost',
    phone: '415-123-4567',
});

address.save().then(console.log);
