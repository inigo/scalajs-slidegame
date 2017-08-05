scalajs-slider is a sliding block puzzle in ScalaJS.

It's inspired by https://github.com/nespera/elm-slide - the equivalent in Elm, by my colleague Chris.

## How to run locally

    bin/sbt
    > ~fastOptJS`

Navigate to: http://localhost:12345/js/target/scala-2.12/classes/index-dev.html

The page will refresh on every change to the code.

Use `sbt fullOptJS` and open `index-opt.html` for the optimized version.


## Live version

https://s3.eu-west-2.amazonaws.com/scalajs-slidegame/resources/index-opt.html

To upload a new version:

    bin/sbt
    > fullOptJs
    > s3-upload

(this depends on having appropriate S3 credentials)

The S3 bucket is set up with a public read policy:

    {
      "Version":"2012-10-17",
      "Statement":[{
        "Sid":"PublicReadGetObject",
            "Effect":"Allow",
          "Principal": "*",
          "Action":["s3:GetObject"],
          "Resource":["arn:aws:s3:::scalajs-minesweeper/*"
          ]
        }
      ]
    }


## License

Copyright (C) 2017 Inigo Surguy.

Licensed under the GNU General Public License v3 or any later version.
