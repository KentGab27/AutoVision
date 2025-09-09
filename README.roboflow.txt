
Annotation V5.1 - v4 NewVersionV0.3
==============================

This dataset was exported via roboflow.com on September 2, 2025 at 3:53 AM GMT

Roboflow is an end-to-end computer vision platform that helps you
* collaborate with your team on computer vision projects
* collect & organize images
* understand and search unstructured image data
* annotate, and create datasets
* export, train, and deploy computer vision models
* use active learning to improve your dataset over time

For state of the art Computer Vision training notebooks you can use with this dataset,
visit https://github.com/roboflow/notebooks

To find over 100k other datasets and pre-trained models, visit https://universe.roboflow.com

The dataset includes 10886 images.
Damaged-head-light-damaged-tail-light-Damage-cars-jJZ7-RGlx are annotated in YOLOv8 Oriented Object Detection format.

The following pre-processing was applied to each image:
* Auto-orientation of pixel data (with EXIF-orientation stripping)
* Resize to 640x640 (Fit (black edges))

The following augmentation was applied to create 3 versions of each source image:
* 50% probability of horizontal flip
* 50% probability of vertical flip
* Equal probability of one of the following 90-degree rotations: none, clockwise, counter-clockwise, upside-down
* Randomly crop between 0 and 13 percent of the image
* Random rotation of between -15 and +15 degrees
* Random brigthness adjustment of between -8 and +8 percent
* Random Gaussian blur of between 0 and 0.7 pixels


