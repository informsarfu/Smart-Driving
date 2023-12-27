import cv2
import warnings
import os
warnings.filterwarnings("ignore")

KNOWN_DISTANCE = 48
CAR_WIDTH = 20

FONTS = cv2.FONT_HERSHEY_TRIPLEX

def detect_object(object):
    classes, scores, boxes = model.detect(object,0.4,0.3)
    data_list =[]
    for (classid, score, box) in zip(classes, scores, boxes):
        cv2.rectangle(object, box,(0,0,255), 2)
        cv2.putText(object,"{}:{}".format(class_names[classid],format(score,'.2f')), (box[0], box[1]-14), FONTS,0.6,(0,255,0), 3)

        if classid ==2: #car
            data_list.append([class_names[classid], box[2], (box[0], box[1]-2)])
    return data_list

def cal_distance(f,W,w):
    return (w * f) / W 

def cal_focalLength(d, W, w):
    return (W * d) / w*2

class_names = []
with open("classes.txt", "r") as objects_file:
    class_names = [e_g.strip() for e_g in objects_file.readlines()]

yoloNet = cv2.dnn.readNet('yolov4-tiny.weights', 'yolov4-tiny.cfg')

model = cv2.dnn_DetectionModel(yoloNet)
model.setInputParams(size=(416, 416), scale=1/255, swapRB=True)

car_image_path = os.path.join("src", "car.jpg")

car_data = detect_object(cv2.imread(car_image_path))
print(car_data)
car_width_in_rf = car_data[0][1]
print(car_width_in_rf)

"""keyboard_data = detect_object(cv2.imread(kb_image_path))
#print(keyboard_data)
keyboard_width_in_rf = keyboard_data[1][1]"""

focal_car = cal_focalLength(KNOWN_DISTANCE, CAR_WIDTH, car_width_in_rf)

try:
    capture = cv2.VideoCapture(0)
    while True:
        _,frame = capture.read()

        data = detect_object(frame) 
        for d in data:
            print(d)
            if d[0] == 'car':
                distance = cal_distance(focal_car, CAR_WIDTH, d[1])
                x,y = d[2]
            cv2.rectangle(frame, (x,y-3), (x+150, y+23),(255,255,255),-1)
            cv2.putText(frame,f"Distance:{format(distance,'.2f')}inchs", (x+5,y+13), FONTS, 0.45,(255,0,0), 2)
            if distance <= 20:
                f = open("myfile.txt", "a")
                f.write("New content")
                f.close()
            print("Distance of {} is {} inchs".format(d[0],distance))

        cv2.imshow('frame',frame)
        exit_key_press = cv2.waitKey(1)

        if exit_key_press == ord('q'):
            break

    capture.release()
    cv2.waitKey(0)
    cv2.destroyAllWindows()
except cv2.error:
    print("Select the WebCam or Camera index properly, in my case it is 2","red")
