from ultralytics import YOLO

if __name__ == "__main__":
    model = YOLO('yolov8n.pt')

    model.train(
        data=r'E:\Kent Files Disc E\YOLO V8 FILES\Tests\Final_App_V1.2\data.yaml',
        imgsz=640,
        epochs=200,
        device=0,
        patience=20,
        batch=-1,
        cos_lr=True,
        cache=False,
        # --- augmentation ---
        degrees=10,
        shear=2,
        translate=0.10,      
        scale=0.5,         
        fliplr=0.5,
        flipud=0.0,          
        hsv_h=0.015,         
        hsv_s=0.70,
        hsv_v=0.40
    )
