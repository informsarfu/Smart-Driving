function calculateFuelStatus(estimatedTime, distance, drivingScore, mileagePerGallon, fuelLeft) {
    const fuelRequired = (0.01 * drivingScore + 0.005 * estimatedTime) * distance / mileagePerGallon;
    const extraFuelRequired = Math.ceil(fuelRequired - fuelLeft);

    if (fuelLeft >= fuelRequired) {
        return "Fuel left is sufficient.";
    } else {
        return `Fuel left is insufficient. Additional ${extraFuelRequired} gallons required.`;
    }
}

const estimatedTime = 2;
const distance = 550;
const drivingScore = 75;
const mileagePerGallon = 30;
const fuelLeft = 10;

const fuelStatus = calculateFuelStatus(estimatedTime, distance, drivingScore, mileagePerGallon, fuelLeft);
console.log(fuelStatus);