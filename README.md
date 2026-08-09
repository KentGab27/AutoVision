# Auto Vision: A YOLO - Based External Sedan car Damage Detection and Cost Estimation System 

Paper DOI: http://dx.doi.org/10.1117/12.3115838

## Abstract
This study presents the development and evaluation of Auto Vision: A YOLO-based External Sedan Damage Detection and Cost Estimation System, a smartphone application designed to automate the process of vehicle inspection. The application integrates the YOLOv8 convolutional neural network (CNN) to detect and localize exterior damages on sedan vehicles, while a Decision Tree model handles repair cost estimates based on the damaged components identified. This combination of advanced computer vision and machine learning techniques provides a practical method for estimating financial implications. To evaluate system performance, the model was tested using standard metrics including F1 confidence, precision, recall, mean average precision (mAP), and accuracy. Results indicated that the model achieved good performance under optimal conditions, reliably identifying and localizing damaged parts despite challenges in viewing angles and environmental variations. These findings demonstrate the potential of Auto Vision as a reliable and cost-effective tool for external sedan damage detection, offering significant benefits in efficiency, and accessibility for both users and automotive service providers. 

## Description
Auto Vision is an AI-powered mobile application that automates sedan damage assessment. It uses YOLOv8 to detect and classify exterior vehicle damage and a Decision Tree algorithm to estimate repair costs, providing a faster and more consistent alternative to traditional vehicle inspection methods.

## Conceptual Framework
<p align="center">
  <img width="683" height="386" alt="Screenshot (5613)" src="https://github.com/user-attachments/assets/c858ce57-6840-443e-958a-1b69e2f7da45" />
</p>

Users upload a vehicle photo, which is processed by a trained **YOLOv8 model** to detect and identify exterior damage. The detected damage is matched with repair cost and insurance datasets, then analyzed by a **cost estimation algorithm** to generate an estimated repair cost and damage report.

## Results
### Severity Detection
<p align="center">
  <img width="590" height="391" alt="Screenshot (5614)" src="https://github.com/user-attachments/assets/4d82795b-144c-4317-bc8d-7d2b3c74bdc8" />
</p>

### Confusion Matrix
<p align="center">
  <img width="751" height="628" alt="Screenshot (5615)" src="https://github.com/user-attachments/assets/b941585b-0bc6-4904-87be-c875e5e5e1fa" />
</p>

### Training Metrics
<p align="center">
  <img width="717" height="350" alt="Screenshot (5616)" src="https://github.com/user-attachments/assets/396e3a8f-8d18-4531-9200-4dab27d0bc80" />
</p>

### F1 Confidence Curve
<p align="center">
  <img width="567" height="372" alt="Screenshot (5617)" src="https://github.com/user-attachments/assets/30a4c422-5e88-491f-92fa-5baf418f44ac" />
</p>

## Application
<p align="center">
  <img width="501" height="555" alt="Screenshot (5618)" src="https://github.com/user-attachments/assets/be510adc-bda2-4f04-8a29-0edb46ab7897" />
</p>


