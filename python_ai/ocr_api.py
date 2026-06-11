from flask import Flask, request, jsonify
import easyocr

app = Flask(__name__)

reader = easyocr.Reader(['en'])

@app.route('/ocr', methods=['POST'])
def ocr():

    image_path = request.json.get("image_path")

    try:
        result = reader.readtext(image_path, detail=0)

        extracted_text = " ".join(result)

        return jsonify({
            "success": True,
            "text": extracted_text
        })

    except Exception as e:
        return jsonify({
            "success": False,
            "error": str(e)
        })

if __name__ == '__main__':
    app.run(port=5000)