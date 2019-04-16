import cv2
from align_custom import AlignCustom
from face_feature import FaceFeature
from mtcnn_detect import MTCNNDetect
from tf_graph import FaceRecGraph
import argparse
import sys
import json
import time
import numpy as np
import glob
from pathlib import Path
from PIL import Image



    
def camera_recog(url):
    
    FRGraph = FaceRecGraph();
    MTCNNGraph = FaceRecGraph();
    aligner = AlignCustom();
    extract_feature = FaceFeature(FRGraph)
    face_detect = MTCNNDetect(MTCNNGraph, scale_factor=2);    
    vs = cv2.VideoCapture(url);
    detected = []
    response = {}
    detect_flag = 0
    breakflag=0
    while True:    
        ret,frame = vs.read();  
        time.sleep(.300) ## Wait for 300 milliseconds
        if ret:
            rects, landmarks = face_detect.detect_face(frame,40); 
            #change the second parameter to adjust the distance, reduce the number to increase the detection range
            #min face size is set to 80x80
            aligns = []
            positions = []
            for (i, rect) in enumerate(rects):
                aligned_face, face_pos = aligner.align(160,frame,landmarks[:,i])
                if len(aligned_face) == 160 and len(aligned_face[0]) == 160:
                    aligns.append(aligned_face)
                    positions.append(face_pos)   
                else: 
                    print("Align face failed") #log        
            
            if(len(aligns) > 0):
                features_arr = extract_feature.get_features(aligns)
                recog_data = findPeople(features_arr,positions)          
               
                for (i,rect) in enumerate(rects):
                    # cv2.rectangle(frame,(rect[0],rect[1]),(rect[2],rect[3]),(255,0,0)) #draw bounding box for the face
                    # cv2.putText(frame,recog_data[i][0],(rect[0],rect[1]),cv2.FONT_HERSHEY_SIMPLEX,1,(255,255,255),1,cv2.LINE_AA)
                    if(recog_data[i][0]!="Unknown"):
                        detect_flag = 1
                        if(recog_data[i][0] not in detected):
                             detected.append(recog_data[i][0])
                        else:
                            breakflag=1
                            break
                        response = {
                            "Flag": 1,
                            "Name": detected
                        }
                if(breakflag):                
                    break

        else:
            break
    if(detect_flag):
        print(json.dumps(response))
        return(json.dumps(response))
    else:
        response = {
                "Flag": 0,
                "Name": "Unknown"
        }
        print(json.dumps(response))
        return(json.dumps(response))
   
        
        
        
'''
facerec_128D.txt Data Structure:
{
"Person ID": {
    "Center": [[128D vector]],
    "Left": [[128D vector]],
    "Right": [[128D Vector]]
    }
}
This function basically does a simple linear search for 
^the 128D vector with the min distance to the 128D vector of the face on screen
'''


def findPeople(features_arr, positions, thres = 0.6, percent_thres = 85):
    '''
    :param features_arr: a list of 128d Features of all faces on screen
    :param positions: a list of face position types of all faces on screen
    :param thres: distance threshold
    :return: person name and percentage
    '''
    f = open('./facerec_128D.txt','r')
    data_set = json.loads(f.read());
    returnRes = [];
    
    for (i,features_128D) in enumerate(features_arr):
        result = "Unknown";
        smallest = sys.maxsize
        
        for person in data_set.keys():
            person_data = data_set[person][positions[i]];
            for data in person_data:
                distance = np.sqrt(np.sum(np.square(data-features_128D)))
                if(distance < smallest):
                    smallest = distance;
                    result = person;
                    pos = positions[i];
        percentage =  min(100, 100 * thres / smallest)
        if percentage <= percent_thres :
            result = "Unknown"
        returnRes.append((result,percentage,pos))
   
    return returnRes    

