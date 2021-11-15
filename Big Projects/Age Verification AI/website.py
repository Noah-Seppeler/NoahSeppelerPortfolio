
# My two categories
X = "over"
Y = "under"

# Two example images for the website, they are in the static directory next 
# where this file is and must match the filenames here
sampleX='static/over.jpg'
sampleY='static/under.jpg'

# Where I will keep user uploads
UPLOAD_FOLDER = 'static/uploads'
# Allowed files
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
# Machine Learning Model Filename
ML_MODEL_FILENAME = 'saved_model_more.h5'

from datetime import datetime

#Load operation system library
import os
import shutil

#website libraries
from flask import render_template
from flask import Flask, flash, request, redirect, url_for
from werkzeug.utils import secure_filename

#Load math library
import numpy as np

#Load machine learning libraries
from tensorflow.keras.preprocessing import image
from keras.preprocessing.image import ImageDataGenerator
from keras.models import load_model
from tensorflow.compat.v1.keras.backend import set_session
import tensorflow as tf


# Create the website object
app = Flask(__name__)

def load_model_from_file():
    #Set up the machine learning session
    tf.compat.v1.disable_eager_execution()
    mySession = tf.compat.v1.Session()
    set_session(mySession)
    myModel = load_model(ML_MODEL_FILENAME)
    myGraph = tf.compat.v1.get_default_graph()
    return (mySession,myModel,myGraph)

#Try to allow only images
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

#Define the view for the top level page
@app.route('/index', methods=['GET', 'POST'])
@app.route('/', methods=['GET', 'POST'])
def upload_file():
    #Initial webpage load
    if request.method == 'GET' :
        return render_template('index.html',myX=X,myY=Y,mySampleX=sampleX,mySampleY=sampleY)
    else: # if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser may also
        # submit an empty part without filename
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        # If it doesn't look like an image file
        if not allowed_file(file.filename):
            flash('I only accept files of type'+str(ALLOWED_EXTENSIONS))
            return redirect(request.url)
        #When the user uploads a file with good parameters
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return redirect(url_for('uploaded_file', filename=filename))

@app.route('/login')
def get_statistics():
    return render_template('statistics.html')

@app.route('/archive')
def get_archive():
    return render_template('archive.html', len=len(results), results=results)

@app.route('/about')
def get_about():
    return render_template('about.html')

@app.route('/charts')
def get_charts():
    #getting number of people over and under
    under, over = 0, 0
    for i in results:
        if i.find("under") != -1:
            under+=1
        else:
            over+=1;
    
    #get data for time graph
    undertimes = [0] * 24
    overtimes = [0] * 24
    
    for i in times:
        if i[0] == 'under':
            undertimes[int(i[1])] += 1
        else:
            overtimes[int(i[1])] += 1
    
    return render_template('charts.html', undernumber=under, overnumber=over,
                           undertime=undertimes, overtime=overtimes)

def get_accuracy(test_image):
    
    datagen = ImageDataGenerator(
        shear_range=0.2,
        zoom_range = 0.2,
        horizontal_flip=True)
    shutil.rmtree('acc')
    os.makedirs('acc')
    
    i = 0
    b = datagen.flow(test_image,
                 batch_size=1,
                 save_to_dir='acc',
                 save_prefix='',
                 save_format='jpeg')[0]
    
    mySession = app.config['SESSION']
    myModel = app.config['MODEL']
    myGraph = app.config['GRAPH']
    set_session(mySession)
    with myGraph.as_default():
        oldCount = 0
        youngCount = 0
        for i in range(0, 100):
            b = datagen.flow(test_image,
                 batch_size=1,
                 save_to_dir='acc',
                 save_prefix='',
                 save_format='jpeg')[0]
            
            result = myModel.predict(b)
            
            if result[0] < .5 :
                oldCount += 1
            else:
                youngCount += 1
    
    return (oldCount, youngCount)
    


@app.route('/uploads/<filename>')
def uploaded_file(filename):
    now = datetime.now()
    current_time = now.strftime("%H")
    
    test_image = image.load_img(UPLOAD_FOLDER+"/"+filename,target_size=(128,128))
    test_image = image.img_to_array(test_image)
    test_image = np.expand_dims(test_image, axis=0)

    mySession = app.config['SESSION']
    myModel = app.config['MODEL']
    myGraph = app.config['GRAPH']
    with myGraph.as_default():
        set_session(mySession)
        result = myModel.predict(test_image)
        image_src = "/"+UPLOAD_FOLDER +"/"+filename
        if result[0] < 0.5 :
            answer = "<div class='archiveimage'><img width='256' height='256' src='"+image_src+"' class='img-thumbnail' /><h4>guess:"+X+"</h4></div>"
            times.append(("over", current_time))
        else:
            answer = "<div class='archiveimage'><img width='256' height='256' src='"+image_src+"' class='img-thumbnail' /><h4>guess:"+Y+"</h4></div>"
            times.append(("under", current_time))
        
        accuracy = get_accuracy(test_image)
        
        print(accuracy)
        results.append(answer)
        return render_template('answer.html', myAnswer=answer, myAccuracy=max(accuracy[0], accuracy[1]))
        ##return render_template('index.html',myX=X,myY=Y,mySampleX=sampleX,mySampleY=sampleY,len=len(results),results=results)


def main():
    (mySession,myModel,myGraph) = load_model_from_file()
    
    app.config['SECRET_KEY'] = 'super secret key'
    
    app.config['SESSION'] = mySession
    app.config['MODEL'] = myModel
    app.config['GRAPH'] = myGraph

    app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
    app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024 #16MB upload limit
    app.run()

# Create a running list of results
results = []
times = []

#Launch everything
main()
