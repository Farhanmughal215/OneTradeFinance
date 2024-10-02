import logging
import boto3
from botocore.config import Config
from botocore.exceptions import ClientError
import os
import sys
import threading

my_config = Config(
    region_name='ap-northeast-1',
    signature_version='v4',
    retries={
        'max_attempts': 10,
        'mode': 'standard'
    }
)


class ProgressPercentage(object):

    def __init__(self, filename):
        self._filename = filename
        self._size = float(os.path.getsize(filename))
        self._seen_so_far = 0
        self._lock = threading.Lock()

    def __call__(self, bytes_amount):
        # To simplify, assume this is hooked up to a single filename
        with self._lock:
            self._seen_so_far += bytes_amount
            percentage = (self._seen_so_far / self._size) * 100
            sys.stdout.write(
                "\r%s  %s / %s  (%.2f%%)" % (
                    self._filename, self._seen_so_far, self._size,
                    percentage))
            sys.stdout.flush()

def get_bucket_acl(bucket):
    s3_client = boto3.client('s3',
                             aws_access_key_id='AKIA2H5CORO6MYTUJDR5',
                             aws_secret_access_key='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',
                             config=my_config)
    result = s3_client.get_bucket_acl(Bucket=bucket)
    print(result)



def upload_file(file_name, bucket, object_name=None):
    """Upload a file to an S3 bucket

    :param file_name: File to upload
    :param bucket: Bucket to upload to
    :param object_name: S3 object name. If not specified then file_name is used
    :return: True if file was uploaded, else False
    """

    # If S3 object_name was not specified, use file_name
    if object_name is None:
        object_name = os.path.basename(file_name)

    # Upload the file
    s3_client = boto3.client('s3',
                             aws_access_key_id='AKIA2H5CORO6MYTUJDR5',
                             aws_secret_access_key='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',
                             config=my_config)
    try:
        response = s3_client.upload_file(file_name,
                                         bucket,
                                         object_name,
                                         ExtraArgs={'ACL': 'public-read'},
                                         Callback=ProgressPercentage(file_name))
    except ClientError as e:
        logging.error(e)
        return False
    return True




# get_bucket_acl('xbit-jarb')

upload_file(os.path.join('target', 'node-0.0.2-SNAPSHOT.jar'), 'xbit-jarb')
