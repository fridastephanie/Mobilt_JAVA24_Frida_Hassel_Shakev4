# Shake App

This project was developed to learn about **gyroscope**, **accelerometer**, and other sensors used in mobile app development. The app provides real-time feedback and visual representations of sensor data to help users understand how these sensors work in practice.

The project was developed using **Android Studio**, and tested on a **Pixel 4 emulator** with **API level 33 "Tiramisu", Android 13.0**.

## Features

1. **Gyroscope Control**:  
   - A **ToggleButton** is used to enable or disable the gyroscope. When enabled, the Z-rotation data is displayed and an arrow image rotates based on the phones rotation. When disabled, the arrow stops rotating and the gyroscope data disappears.

2. **Sensitivity Adjustment**:  
   - A **SeekBar** allows users to adjust the sensitivity of the motion detection. This gives users the flexibility to make the app more or less sensitive to movements.

3. **Accelerometer Data**:  
   - The app displays the **X**, **Y**, and **Z** values from the accelerometer, along with the movement intensity based on the changes in these values.

4. **Background Color Change**:  
   - The background color changes based on the phones tilt.

5. **Toast Alerts for Fast Movements or Rotations**:  
   - **Toast** messages are shown when the phone moves rapidly or rotates quickly, based on the accelerometer and gyroscope data.

6. **Light Sensor**:  
   - The app uses the light sensor to adjust screen brightness, dimming in dark environments and brightening in well-lit conditions.

## Project Configuration

- **Min SDK**: 31
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java Version**: 11

### Emulator Information
- **Emulator Used**: Google Pixel 4
- **API Level**: 33

## Installation Guide

1. **Clone the repository**:
   ```bash
   git clone  https://github.com/fridastephanie/Mobilt_JAVA24_Frida_Hassel_Shakev4.git

2. Open in Android Studio:
Open Android Studio and select Open an existing project.

3. Sync Gradle:
Android Studio will sync the project automatically.

4. Select Emulator:
Choose the Pixel 4 Emulator (API 33) or your device.

5. Run the Project:
Click the Run button in Android Studio.

#### Troubleshooting
*Sensor Issues: Ensure your device or emulator has the required sensors.    
Java Version: Use Java 11 for compatibility.  
Android SDK: Ensure you have the API Level 33 SDK installed.*
