# our base build image
FROM nvidia/cuda:10.1-devel as cuda-build

##########
# opencv #
##########
RUN apt-get update && apt-get install -y build-essential cmake git libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libdc1394-22-dev wget unzip
WORKDIR /
RUN wget -O/tmp/opencv-4.1.0.zip https://github.com/opencv/opencv/archive/4.1.0.zip && unzip /tmp/opencv-4.1.0.zip && rm /tmp/opencv-4.1.0.zip
WORKDIR opencv-4.1.0/build
RUN cmake -D CMAKE_BUILD_TYPE=Release -D OPENCV_GENERATE_PKGCONFIG=YES -D CMAKE_INSTALL_PREFIX=/usr/local .. && make && mkdir installdir && make install && make install DESTDIR=installdir && ln -sf /usr/local/lib/pkgconfig/opencv4.pc /usr/local/lib/pkgconfig/opencv.pc
################
# yolo darknet #
################
WORKDIR /
RUN git clone https://github.com/AlexeyAB/darknet.git
WORKDIR darknet
RUN sed -i 's/GPU=0/GPU=1/;s/OPENCV=0/OPENCV=1/;s/LIBSO=0/LIBSO=1/' Makefile
RUN make

###############
# fresh image #
###############
FROM nvidia/cuda:10.1-devel
RUN apt-get update && apt-get install -y python-pip libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev python-dev libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libdc1394-22-dev
COPY --from=cuda-build /opencv-4.1.0/build/installdir /
COPY --from=cuda-build /darknet /darknet
COPY requirements.txt .
RUN pip install -r requirements.txt

WORKDIR /darknet
ENV LD_LIBRARY_PATH /usr/local/cuda-10.1/compat/:/darknet
COPY opencv_yolo/darknet_video.py .
COPY opencv_yolo/customDarknet.py .
COPY yolov3-models yolov3-models
RUN for f in yolov3-models/*/files/*; do d=`dirname $f` && mv $f $d/..; done

ENV MODEL_PATH /darknet/yolov3-models/modelFromCOM19/
ENTRYPOINT ["python", "-u", "./darknet_video.py"]
CMD []
