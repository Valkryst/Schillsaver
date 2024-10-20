[![Java CI with Maven](https://github.com/Valkryst/Schillsaver/actions/workflows/maven.yml/badge.svg)](https://github.com/Valkryst/Schillsaver/actions/workflows/maven.yml)
[![CodeQL](https://github.com/Valkryst/Schillsaver/actions/workflows/codeql.yml/badge.svg)](https://github.com/Valkryst/Schillsaver/actions/workflows/codeql.yml)

Schillsaver is a tool that allows you to encode one or more files into a single video. This video, consisting of black
and white squares, represents the data of the input file(s). The video can then be uploaded to a hosting site, such
as YouTube, for future re-download and decoding back into the original file(s).

This provides a novel approach to free file storage.

While Schillsaver was developed as a proof-of-concept, its usage is entirely up to you. However, we do not explicitly
recommend using YouTube or any other platform for free file storage.

## Table of Contents

* [Requirements](https://github.com/Valkryst/Schillsaver#requirements)

## Requirements

* [FFMPEG](https://ffmpeg.org/download.html)
* [Java 21](https://github.com/Valkryst/Install_Java)
* [Schillsaver](https://github.com/Valkryst/Schillsaver/releases)

## Notes

* For successful decoding, you must use the same block size and resolution settings that you used during encoding. For
  example, if you've encoded a video with block size 8 at 1920x1080, then it must be decoded with block size 8 and
  resolution 1920x1080. Any other block size or resolution will result in a corrupt decode.

* On Linux, you may need to launch the program using the `java -jar Schillsaver.jar` command.

* When downloading your stored videos, ensure that you download the video at the same resolution that you encoded the
  video at. For example, if you've encoded a 1080p video, then download it as 1080p. Downloading it at a lower
  resolution (e.g. 720p) will result in a corrupt decode.

* Windows' default zip utility will likely throw an error, when you open a decoded zip file. I recommend using 7zip to
  instead, as it does not have this issue.