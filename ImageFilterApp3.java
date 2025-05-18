package example02;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class ImageFilterApp3 extends JFrame{

	private BufferedImage originalImage;
	private JSlider brightnessSlider;
	private BufferedImage filteredImage;  //마지막 필터 적용된 이미지 저장용
	private JLabel originalLabel;
	private JLabel filteredLabel;
	private JSplitPane splitPane;
	
	public ImageFilterApp3() {
		super("이미지 필터 앱");
		setLayout(new BorderLayout());
		
		//이미지 출력 라벨
		originalLabel = new JLabel("왼쪽: 원본", JLabel.CENTER);
		filteredLabel = new JLabel("오른쪽: 필터 적용", JLabel.CENTER);
		
		JScrollPane scroll1 = new JScrollPane(originalLabel);
		JScrollPane scroll2 = new JScrollPane(filteredLabel);
		
		//스플릿 패널에 스크롤 패널들을 넣기
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2);
		splitPane.setDividerLocation(400);  //가운데 선 나누는 위치
		add(splitPane, BorderLayout.CENTER);
		
		//버튼 영역
		JPanel buttonPanel = new JPanel();
		
		JButton openBtn = new JButton("이미지 열기");
		JButton redBtn = new JButton("빨강 필터");
		JButton greenBtn = new JButton("초록 필터");
		JButton blueBtn = new JButton("파랑 필터");
		JButton grayBtn = new JButton("흑백 필터");
		JButton saveBtn = new JButton("저장");
		
		buttonPanel.add(openBtn);
		buttonPanel.add(redBtn);
		buttonPanel.add(greenBtn);
		buttonPanel.add(blueBtn);
		buttonPanel.add(grayBtn);
		buttonPanel.add(saveBtn);
		
		brightnessSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		brightnessSlider.setMajorTickSpacing(50);
		brightnessSlider.setMinorTickSpacing(10);
		brightnessSlider.setPaintTicks(true);
		brightnessSlider.setPaintLabels(true);
		
		add(brightnessSlider, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);
		
		//버튼 이벤트 등록
		openBtn.addActionListener(e -> openImage());
		redBtn.addActionListener(e -> applyFilter("red"));
		greenBtn.addActionListener(e -> applyFilter("green"));
		blueBtn.addActionListener(e -> applyFilter("blue"));
		grayBtn.addActionListener(e -> applyFilter("gray"));
		saveBtn.addActionListener(e -> saveImage());
		
		//슬라이더 값이 바뀔 때 이벤트 처리
		brightnessSlider.addChangeListener(e -> {
			if(filteredImage != null) {
				BufferedImage adjusted = adjustBrightness(filteredImage, brightnessSlider.getValue());
				filteredLabel.setIcon(new ImageIcon(adjusted));
			}
		});
		
		//MouseListener 등록
		filteredLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Icon icon = filteredLabel.getIcon();
				if(icon instanceof ImageIcon) {
					ImageIcon imageIcon = (ImageIcon)icon;
					Image img = imageIcon.getImage();
					
					int iconWidth = imageIcon.getIconWidth();
					int iconHeight = imageIcon.getIconHeight();
					int labelWidth = filteredLabel.getWidth();
					int labelHeight = filteredLabel.getHeight();
					
					//이미지가 중앙에 위치해 있을 때 생기는 여백 계산
					int offsetX = (labelWidth - iconWidth) / 2;
					int offsetY = (labelHeight - iconHeight) / 2;
					
					//실제 클릭한 이미지 내부 좌표 계산
					int x = e.getX() - offsetX;
					int y = e.getY() - offsetY;
					
					//유효한 영역인지 확인
					if(x >= 0 && x < iconWidth && y >= 0 && y < iconHeight) {
						BufferedImage buffered = new BufferedImage(
							img.getWidth(filteredLabel),
							img.getHeight(filteredLabel),
							BufferedImage.TYPE_INT_RGB
						);
						Graphics2D g = buffered.createGraphics();
						g.drawImage(img, 0, 0, filteredLabel);
						g.dispose();
						
						int rgb = buffered.getRGB(x, y);
						Color color = new Color(rgb);
						int r = color.getRed();
						int gColor = color.getGreen();
						int b = color.getBlue();
						String hex = String.format("#%02X%02X%02X", r, gColor, b);
						
						JOptionPane.showMessageDialog(filteredLabel,
							"좌표: (" + x + ", " + y + ")\n" + 
							"RGB: (" + r + ", " + gColor + ", " + b + ")\n" + 
							"HEX: " + hex,
							 "픽셀 정보",
							 JOptionPane.INFORMATION_MESSAGE);
					}	
				}
			};
		});
		
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//이미지 열기 함수
	public void openImage() {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			try {
				File file = chooser.getSelectedFile();
				originalImage = ImageIO.read(file);
				
				//원본 이미지 출력
				ImageIcon icon = getScaledImage(originalImage, originalLabel.getWidth(), originalLabel.getHeight());
				originalLabel.setIcon(icon);
				filteredLabel.setIcon(null);   //필터 전이라서 초기화
				brightnessSlider.setValue(0);  //밝기 초기화
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "이미지 불러오기 실패");
			}
		}
	}
	
	//필터 적용 함수
	public void applyFilter(String type) {
		if(originalImage == null) {
			return;
		}
		
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		BufferedImage filtered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for(int y=0; y<h; y++) {
			for(int x=0; x<w; x++) {
				Color c = new Color(originalImage.getRGB(x, y));
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();
				
				Color newColor;
				switch (type) {
					case "red":
						newColor = new Color(r, 0, 0);
						break;
					case "green":
						newColor = new Color(0, g, 0);
						break;
					case "blue":
						newColor = new Color(0, 0, b);
						break;
					case "gray":
						int gray = (int)(0.299*r + 0.587*g + 0.114*b);
						newColor = new Color(gray, gray, gray);
						break;
					default:
						newColor = c;
				}
				filtered.setRGB(x, y, newColor.getRGB());
			}
		}
		filteredImage = filtered;  //슬라이더에서 참조하도록 저장
		ImageIcon filteredIcon = getScaledImage(filtered, filtered.getWidth(), filtered.getHeight());
		filteredLabel.setIcon(filteredIcon);
	}
	
	//저장 함수(확장)
	public void saveImage() {
		if(filteredLabel.getIcon() == null) {
			JOptionPane.showMessageDialog(this, "저장할 이미지가 없습니다.");
			return;
		}
		
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			
			//이미지Icon에서 BufferedImage 추출
			Icon icon = filteredLabel.getIcon();
			if(icon instanceof ImageIcon) {
				Image img = ((ImageIcon)icon).getImage();
				
				//이미지 크기를 미리 지정(null 없이)
				int width = img.getWidth(filteredLabel);
				int height = img.getHeight(filteredLabel);
				
				//BufferedImage로 복사
				BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = buffered.createGraphics();
				g.drawImage(img, 0, 0, filteredLabel);
				g.dispose();
				
				try {
					ImageIO.write(buffered, "PNG", file);
					JOptionPane.showMessageDialog(this, "저장 완료");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "저장 실패");
				}
			}
		}
	}
	
	//밝기 조절 함수 추가(확장)
	public BufferedImage adjustBrightness(BufferedImage img, int offset) {
		 int w = img.getWidth();
		 int h = img.getHeight();
		 BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		 
		 for(int y=0; y<h; y++) {
			 for(int x=0; x<w; x++) {
				 Color c = new Color(img.getRGB(x, y));
				 int r = clamp(c.getRed() + offset);
				 int g = clamp(c.getGreen() + offset);
				 int b = clamp(c.getBlue() + offset);
				 result.setRGB(x, y, new Color(r, g, b).getRGB());
			 }
		 }
		 return result;
	}
	
	//clamp 함수, 0~255로 제한(확장)
	public int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}
	//실행
	public static void main(String[] args) {
		//Swing 프로그램 안전하게 시작!
		SwingUtilities.invokeLater(() -> new ImageFilterApp3());
	}
	
	//유틸 함수 추가
	private ImageIcon getScaledImage(Image srcImg, int maxWidth, int maxHeight) {
		int srcWidth = srcImg.getWidth(null);
		int srcHeight = srcImg.getHeight(null);
		
		//원본 비율 유지하면서 축소
		double scale = Math.min((double)maxWidth/srcWidth, (double)maxHeight/srcHeight);
		int newWidth = (int)(srcWidth * scale);
		int newHeight = (int)(srcHeight * scale);
		
		Image scaled = srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
